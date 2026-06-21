package com.iot.platform.controller;

import com.iot.platform.dto.ApiResponse;
import com.iot.platform.service.AlertService;
import com.iot.platform.service.DeviceService;
import com.iot.platform.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计看板控制器
 */
@Tag(name = "统计看板", description = "数据统计和可视化")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ProductService productService;
    private final DeviceService deviceService;
    private final AlertService alertService;

    /**
     * 获取统计数据
     */
    @Operation(summary = "获取统计数据")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productService.getAllProducts().size());
        stats.put("totalDevices", deviceService.countTotalDevices());
        stats.put("onlineDevices", deviceService.countOnlineDevices());
        stats.put("todayAlerts", alertService.countTodayAlerts());
        return ApiResponse.success(stats);
    }

    /**
     * 获取产品类型分布（饼图数据）
     */
    @Operation(summary = "获取产品类型分布")
    @GetMapping("/products/distribution")
    public ApiResponse<Map<String, Long>> getProductDistribution() {
        return ApiResponse.success(productService.getProductCountByType());
    }
}
