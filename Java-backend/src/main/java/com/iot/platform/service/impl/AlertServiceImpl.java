package com.iot.platform.service.impl;

import com.iot.platform.entity.AlertRule;
import com.iot.platform.entity.AlertRecord;
import com.iot.platform.repository.AlertRuleRepository;
import com.iot.platform.repository.AlertRecordRepository;
import com.iot.platform.service.AlertService;
import com.iot.platform.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * 告警服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRuleRepository ruleRepository;
    private final AlertRecordRepository recordRepository;
    private final WebSocketService webSocketService;

    @Override
    @Transactional
    public AlertRule createRule(AlertRule rule) {
        rule.setId(generateId("RULE"));
        return ruleRepository.save(rule);
    }

    @Override
    @Transactional
    public AlertRule updateRule(String ruleId, AlertRule rule) {
        AlertRule existing = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("规则不存在: " + ruleId));
        existing.setPropertyName(rule.getPropertyName());
        existing.setConditionExpr(rule.getConditionExpr());
        existing.setAlertContent(rule.getAlertContent());
        existing.setEnabled(rule.getEnabled());
        return ruleRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteRule(String ruleId) {
        ruleRepository.deleteById(ruleId);
    }

    @Override
    public List<AlertRule> getRulesByProductId(String productId) {
        return ruleRepository.findByProductId(productId);
    }

    @Override
    public List<AlertRule> getAllRules() {
        return ruleRepository.findAll();
    }

    @Override
    @Transactional
    public void checkAlert(String deviceId, String productId, String propertyName, Object value) {
        // 根据产品ID和属性名精确查询启用的告警规则
        List<AlertRule> rules = ruleRepository.findByProductIdAndPropertyNameAndEnabled(productId, propertyName, true);
        if (rules.isEmpty()) return;

        double numValue;
        try {
            numValue = Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            // 如果不是数值，进行简单的字符串比较（降级处理）
            for (AlertRule rule : rules) {
                if (rule.getConditionExpr().startsWith("==")) {
                    String expected = rule.getConditionExpr().substring(2).trim();
                    if (expected.equals(value.toString())) {
                        triggerAlertRecord(deviceId, rule, value);
                    }
                }
            }
            return;
        }

        // 使用 Drools 规则引擎动态编译并执行规则
        StringBuilder drlBuilder = new StringBuilder();
        drlBuilder.append("package com.iot.platform.rules;\n\n");
        drlBuilder.append("import com.iot.platform.service.impl.AlertServiceImpl.AlertFact;\n\n");

        for (AlertRule rule : rules) {
            drlBuilder.append("rule \"Rule_").append(rule.getId()).append("\"\n");
            drlBuilder.append("when\n");
            // 例如 rule.getConditionExpr() 是 "> 35"
            drlBuilder.append("    $fact: AlertFact(value ").append(rule.getConditionExpr()).append(")\n");
            drlBuilder.append("then\n");
            drlBuilder.append("    $fact.triggerRule(\"").append(rule.getId()).append("\");\n");
            drlBuilder.append("end\n\n");
        }

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/rules/dynamic_rules.drl", drlBuilder.toString());
        
        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        if (kb.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
            log.error("Drools 规则编译失败: {}", kb.getResults().toString());
            return;
        }

        KieContainer kContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        KieSession kSession = kContainer.newKieSession();

        AlertFact fact = new AlertFact(numValue);
        kSession.insert(fact);
        kSession.fireAllRules();
        kSession.dispose();

        // 遍历所有被 Drools 触发的规则ID
        for (String triggeredRuleId : fact.getTriggeredRuleIds()) {
            rules.stream().filter(r -> r.getId().equals(triggeredRuleId)).findFirst()
                 .ifPresent(rule -> triggerAlertRecord(deviceId, rule, numValue));
        }
    }

    private void triggerAlertRecord(String deviceId, AlertRule rule, Object value) {
        AlertRecord record = new AlertRecord();
        record.setDeviceId(deviceId);
        record.setRuleId(rule.getId());
        record.setAlertContent(rule.getAlertContent());
        record.setStatus("pending");

        AlertRecord saved = recordRepository.save(record);
        log.warn("告警触发: deviceId={}, rule={}, value={}", deviceId, rule.getConditionExpr(), value);

        // 通过WebSocket推送告警
        webSocketService.sendAlertNotification(saved);
    }

    @Override
    public List<AlertRecord> getAlertRecords() {
        return recordRepository.findTop20ByOrderByAlertTimeDesc();
    }

    @Override
    public List<AlertRecord> getAlertRecordsByDeviceId(String deviceId) {
        return recordRepository.findByDeviceIdOrderByAlertTimeDesc(deviceId);
    }

    @Override
    @Transactional
    public void confirmAlert(Long alertId, String remark) {
        AlertRecord record = recordRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("告警记录不存在: " + alertId));
        record.setStatus("confirmed");
        record.setConfirmedTime(LocalDateTime.now());
        record.setRemark(remark);
        recordRepository.save(record);
    }

    @Override
    public long countTodayAlerts() {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return recordRepository.countTodayAlerts(todayStart);
    }

    /**
     * Drools 需要的 Fact 对象
     */
    public static class AlertFact {
        private double value;
        private List<String> triggeredRuleIds = new ArrayList<>();

        public AlertFact(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public void triggerRule(String ruleId) {
            triggeredRuleIds.add(ruleId);
        }

        public List<String> getTriggeredRuleIds() {
            return triggeredRuleIds;
        }
    }

    private String generateId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
