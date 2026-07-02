package com.iot.platform.service;

import com.iot.platform.entity.AlertRecord;
import com.iot.platform.entity.AlertRule;

import java.util.List;

public interface AlertService {
    AlertRule createRule(AlertRule rule);
    AlertRule updateRule(String ruleId, AlertRule rule);
    void deleteRule(String ruleId);
    List<AlertRule> getRulesByProductId(String productId);
    List<AlertRule> getAllRules();
    void checkAlert(String deviceId, String productId, String propertyName, Object value);
    List<AlertRecord> getAlertRecords();
    List<AlertRecord> getAlertRecordsByDeviceId(String deviceId);
    List<AlertRecord> getRecentAlerts(int limit);
    void confirmAlert(Long alertId, String remark);
    long countTodayAlerts();
}
