package com.iot.platform.controller;

import com.iot.platform.annotation.OperationLog;
import com.iot.platform.dto.ApiResponse;
import com.iot.platform.entity.AlertRule;
import com.iot.platform.entity.AlertRecord;
import com.iot.platform.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 告警管理控制器
 */
@Tag(name = "告警管理", description = "告警规则和告警记录")
@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    /**
     * 创建告警规则
     */
    @Operation(summary = "创建告警规则")
    @OperationLog(module = "告警管理", type = "CREATE", description = "创建告警规则")
    @PostMapping("/rules")
    public ApiResponse<AlertRule> createRule(@RequestBody AlertRule rule) {
        return ApiResponse.success(alertService.createRule(rule));
    }

    /**
     * 更新告警规则
     */
    @Operation(summary = "更新告警规则")
    @PutMapping("/rules/{ruleId}")
    public ApiResponse<AlertRule> updateRule(@PathVariable String ruleId, @RequestBody AlertRule rule) {
        return ApiResponse.success(alertService.updateRule(ruleId, rule));
    }

    /**
     * 删除告警规则
     */
    @Operation(summary = "删除告警规则")
    @DeleteMapping("/rules/{ruleId}")
    public ApiResponse<Void> deleteRule(@PathVariable String ruleId) {
        alertService.deleteRule(ruleId);
        return ApiResponse.success(null);
    }

    /**
     * 查询所有告警规则
     */
    @Operation(summary = "查询所有告警规则")
    @GetMapping("/rules")
    public ApiResponse<List<AlertRule>> getAllRules() {
        return ApiResponse.success(alertService.getAllRules());
    }

    /**
     * 查询产品下的告警规则
     */
    @Operation(summary = "查询产品下的告警规则")
    @GetMapping("/rules/product/{productId}")
    public ApiResponse<List<AlertRule>> getRulesByProduct(@PathVariable String productId) {
        return ApiResponse.success(alertService.getRulesByProductId(productId));
    }

    /**
     * 查询告警记录
     */
    @Operation(summary = "查询告警记录")
    @GetMapping("/records")
    public ApiResponse<List<AlertRecord>> getAlertRecords() {
        return ApiResponse.success(alertService.getAlertRecords());
    }

    /**
     * 查询设备的告警记录
     */
    @Operation(summary = "查询设备告警记录")
    @GetMapping("/records/device/{deviceId}")
    public ApiResponse<List<AlertRecord>> getAlertRecordsByDevice(@PathVariable String deviceId) {
        return ApiResponse.success(alertService.getAlertRecordsByDeviceId(deviceId));
    }

    /**
     * 确认告警
     */
    @Operation(summary = "确认告警")
    @OperationLog(module = "告警管理", type = "UPDATE", description = "确认告警")
    @PutMapping("/records/{alertId}/confirm")
    public ApiResponse<Void> confirmAlert(@PathVariable Long alertId,
                                           @RequestBody Map<String, String> body) {
        alertService.confirmAlert(alertId, body.get("remark"));
        return ApiResponse.success(null);
    }

    /**
     * 统计今日告警数量
     */
    @Operation(summary = "统计今日告警数量")
    @GetMapping("/statistics/today")
    public ApiResponse<Long> countTodayAlerts() {
        return ApiResponse.success(alertService.countTodayAlerts());
    }
}
