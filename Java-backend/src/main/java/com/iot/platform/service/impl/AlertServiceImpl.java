package com.iot.platform.service.impl;

import com.iot.platform.entity.AlertRecord;
import com.iot.platform.entity.AlertRule;
import com.iot.platform.repository.AlertRecordRepository;
import com.iot.platform.repository.AlertRuleRepository;
import com.iot.platform.service.AlertService;
import com.iot.platform.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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
        rule.setCreatedTime(LocalDateTime.now());
        rule.setUpdatedTime(LocalDateTime.now());
        return ruleRepository.save(rule);
    }

    @Override
    @Transactional
    public AlertRule updateRule(String ruleId, AlertRule rule) {
        AlertRule existing = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("规则不存在: " + ruleId));
        existing.setPropertyIdentifier(rule.getPropertyIdentifier());
        existing.setOperator(rule.getOperator());
        existing.setThreshold(rule.getThreshold());
        existing.setAlertContent(rule.getAlertContent());
        existing.setEnabled(rule.getEnabled());
        existing.setUpdatedTime(LocalDateTime.now());
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
        List<AlertRule> rules = ruleRepository.findByProductIdAndPropertyIdentifierAndEnabled(productId, propertyName, true);
        if (rules.isEmpty()) return;

        for (AlertRule rule : rules) {
            if (matches(rule.getOperator(), rule.getThreshold(), value)) {
                AlertRecord record = new AlertRecord();
                record.setDeviceId(deviceId);
                record.setProductId(productId);
                record.setRuleId(rule.getId());
                record.setPropertyIdentifier(propertyName);
                record.setActualValue(value.toString());
                record.setAlertContent(rule.getAlertContent());
                record.setStatus("pending");

                AlertRecord saved = recordRepository.save(record);
                log.warn("告警触发: deviceId={}, rule={}, value={}", deviceId, rule.getAlertContent(), value);

                try {
                    webSocketService.sendAlertNotification(saved);
                } catch (Exception e) {
                    log.warn("WebSocket推送告警失败: {}", e.getMessage());
                }
            }
        }
    }

    private boolean matches(String operator, String threshold, Object value) {
        try {
            BigDecimal numValue = new BigDecimal(value.toString());
            BigDecimal numThreshold = new BigDecimal(threshold);
            int cmp = numValue.compareTo(numThreshold);
            switch (operator) {
                case ">": return cmp > 0;
                case ">=": return cmp >= 0;
                case "<": return cmp < 0;
                case "<=": return cmp <= 0;
                case "==": return cmp == 0;
                case "!=": return cmp != 0;
                default: return false;
            }
        } catch (NumberFormatException e) {
            if ("==".equals(operator)) return threshold.equals(value.toString());
            if ("!=".equals(operator)) return !threshold.equals(value.toString());
            return false;
        }
    }

    @Override
    public List<AlertRecord> getAlertRecords() {
        return recordRepository.findRecentAlerts(PageRequest.of(0, 50));
    }

    @Override
    public List<AlertRecord> getAlertRecordsByDeviceId(String deviceId) {
        return recordRepository.findByDeviceIdOrderByAlertTimeDesc(deviceId);
    }

    @Override
    public List<AlertRecord> getRecentAlerts(int limit) {
        return recordRepository.findRecentAlerts(PageRequest.of(0, limit));
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

    private String generateId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
