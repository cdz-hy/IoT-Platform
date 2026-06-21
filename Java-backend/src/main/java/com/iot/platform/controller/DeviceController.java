package com.iot.platform.controller;

import com.iot.platform.annotation.OperationLog;
import com.iot.platform.dto.ApiResponse;
import com.iot.platform.dto.CommandRequest;
import com.iot.platform.dto.DeviceRegisterRequest;
import com.iot.platform.entity.Device;
import com.iot.platform.entity.DeviceConnectLog;
import com.iot.platform.entity.CommandLog;
import com.iot.platform.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 设备管理控制器
 */
@Tag(name = "设备管理", description = "设备注册、查询、命令下发")
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * 注册设备
     */
    @Operation(summary = "注册设备")
    @OperationLog(module = "设备管理", type = "CREATE", description = "注册设备")
    @PostMapping("/register")
    public ApiResponse<Device> registerDevice(@Valid @RequestBody DeviceRegisterRequest request) {
        Device device = deviceService.registerDevice(
                request.getProductId(),
                request.getNodeId(),
                request.getDeviceName()
        );
        return ApiResponse.success("设备注册成功", device);
    }

    /**
     * 更新设备
     */
    @Operation(summary = "更新设备")
    @PutMapping("/{deviceId}")
    public ApiResponse<Device> updateDevice(@PathVariable String deviceId, @RequestBody Device device) {
        return ApiResponse.success(deviceService.updateDevice(deviceId, device));
    }

    /**
     * 删除设备
     */
    @Operation(summary = "删除设备")
    @OperationLog(module = "设备管理", type = "DELETE", description = "删除设备")
    @DeleteMapping("/{deviceId}")
    public ApiResponse<Void> deleteDevice(@PathVariable String deviceId) {
        deviceService.deleteDevice(deviceId);
        return ApiResponse.success(null);
    }

    /**
     * 查询设备详情
     */
    @Operation(summary = "查询设备详情")
    @GetMapping("/{deviceId}")
    public ApiResponse<Device> getDevice(@PathVariable String deviceId) {
        return ApiResponse.success(deviceService.getDeviceById(deviceId));
    }

    /**
     * 查询所有设备
     */
    @Operation(summary = "查询所有设备")
    @GetMapping
    public ApiResponse<List<Device>> getAllDevices() {
        return ApiResponse.success(deviceService.getAllDevices());
    }

    /**
     * 根据产品ID查询设备列表
     */
    @Operation(summary = "根据产品ID查询设备")
    @GetMapping("/product/{productId}")
    public ApiResponse<List<Device>> getDevicesByProduct(@PathVariable String productId) {
        return ApiResponse.success(deviceService.getDevicesByProductId(productId));
    }

    /**
     * 查询设备上下线记录
     */
    @Operation(summary = "查询设备上下线记录")
    @GetMapping("/{deviceId}/connect-logs")
    public ApiResponse<List<DeviceConnectLog>> getConnectLogs(@PathVariable String deviceId) {
        return ApiResponse.success(deviceService.getConnectLogs(deviceId));
    }

    /**
     * 查询最近的上下线记录
     */
    @Operation(summary = "查询最近上下线记录")
    @GetMapping("/connect-logs/recent")
    public ApiResponse<List<DeviceConnectLog>> getRecentConnectLogs() {
        return ApiResponse.success(deviceService.getRecentConnectLogs());
    }

    /**
     * 下发命令
     */
    @Operation(summary = "下发命令")
    @OperationLog(module = "设备管理", type = "COMMAND", description = "下发命令")
    @PostMapping("/command")
    public ApiResponse<CommandLog> sendCommand(@Valid @RequestBody CommandRequest request) {
        CommandLog command = deviceService.sendCommand(
                request.getDeviceId(),
                request.getCommandName(),
                request.getCommandParams()
        );
        return ApiResponse.success("命令下发成功", command);
    }

    /**
     * 查询设备命令记录
     */
    @Operation(summary = "查询设备命令记录")
    @GetMapping("/{deviceId}/commands")
    public ApiResponse<List<CommandLog>> getCommandLogs(@PathVariable String deviceId) {
        return ApiResponse.success(deviceService.getCommandLogs(deviceId));
    }

    /**
     * 统计在线设备数量
     */
    @Operation(summary = "统计在线设备数量")
    @GetMapping("/statistics/online")
    public ApiResponse<Long> countOnlineDevices() {
        return ApiResponse.success(deviceService.countOnlineDevices());
    }

    /**
     * 统计总设备数量
     */
    @Operation(summary = "统计总设备数量")
    @GetMapping("/statistics/total")
    public ApiResponse<Long> countTotalDevices() {
        return ApiResponse.success(deviceService.countTotalDevices());
    }
}
