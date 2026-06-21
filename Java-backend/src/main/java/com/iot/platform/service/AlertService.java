package com.iot.platform.service;

import com.iot.platform.entity.AlertRule;
import com.iot.platform.entity.AlertRecord;

import java.util.List;

/**
 * 告警服务接口
 */
public interface AlertService {

    /** 创建告警规则 */
    AlertRule createRule(AlertRule rule);

    /** 更新告警规则 */
    AlertRule updateRule(String ruleId, AlertRule rule);

    /** 删除告警规则 */
    void deleteRule(String ruleId);

    /** 查询产品下的告警规则 */
    List<AlertRule> getRulesByProductId(String productId);

    /** 查询所有告警规则 */
    List<AlertRule> getAllRules();

    /** 检查设备数据是否触发告警 */
    void checkAlert(String deviceId, String productId, String propertyName, Object value);

    /** 查询告警记录 */
    List<AlertRecord> getAlertRecords();

    /** 查询设备的告警记录 */
    List<AlertRecord> getAlertRecordsByDeviceId(String deviceId);

    /** 确认告警 */
    void confirmAlert(Long alertId, String remark);

    /** 统计今日告警数量 */
    long countTodayAlerts();
}
