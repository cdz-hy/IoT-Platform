package com.iot.platform.controller;

import com.iot.platform.annotation.OperationLog;
import com.iot.platform.dto.ApiResponse;
import com.iot.platform.dto.ShadowUpdateRequest;
import com.iot.platform.entity.DeviceShadow;
import com.iot.platform.service.DeviceShadowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 设备影子控制器
 */
@Tag(name = "设备影子", description = "设备影子管理")
@RestController
@RequestMapping("/shadows")
@RequiredArgsConstructor
public class DeviceShadowController {

    private final DeviceShadowService shadowService;

    /**
     * 获取设备影子
     */
    @Operation(summary = "获取设备影子")
    @GetMapping("/{deviceId}")
    public ApiResponse<DeviceShadow> getShadow(@PathVariable String deviceId) {
        return ApiResponse.success(shadowService.getShadow(deviceId));
    }

    /**
     * 更新设备desired区
     */
    @Operation(summary = "更新设备desired区")
    @OperationLog(module = "设备影子", type = "UPDATE", description = "更新设备期望状态")
    @PutMapping("/desired")
    public ApiResponse<Void> updateDesired(@Valid @RequestBody ShadowUpdateRequest request) {
        shadowService.updateDesired(request.getDeviceId(), request.getDesired());
        return ApiResponse.success(null);
    }

    /**
     * 计算设备delta
     */
    @Operation(summary = "计算设备delta")
    @GetMapping("/{deviceId}/delta")
    public ApiResponse<Map<String, Object>> calculateDelta(@PathVariable String deviceId) {
        return ApiResponse.success(shadowService.calculateDelta(deviceId));
    }
}
