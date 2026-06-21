package com.iot.platform.service.impl;

import com.iot.platform.entity.*;
import com.iot.platform.repository.*;
import com.iot.platform.service.DeviceService;
import com.iot.platform.service.DeviceShadowService;
import com.iot.platform.mqtt.MqttService;
import com.iot.platform.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 设备服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final ProductRepository productRepository;
    private final DeviceConnectLogRepository connectLogRepository;
    private final CommandLogRepository commandLogRepository;
    private final DeviceShadowService shadowService;
    private final MqttService mqttService;
    private final WebSocketService webSocketService;

    @Override
    @Transactional
    public Device registerDevice(String productId, String nodeId, String deviceName) {
        // 检查设备标识码是否已存在
        if (deviceRepository.findByNodeId(nodeId) != null) {
            throw new RuntimeException("设备标识码已存在: " + nodeId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("产品不存在: " + productId));

        Device device = new Device();
        device.setDeviceId(generateId("DEV"));
        device.setNodeId(nodeId);
        device.setDeviceName(deviceName);
        device.setSecret(generateSecret());
        device.setStatus("offline");
        device.setProduct(product);

        Device saved = deviceRepository.save(device);

        // 初始化设备影子
        shadowService.getShadow(saved.getDeviceId());

        log.info("设备注册成功: deviceId={}, nodeId={}", saved.getDeviceId(), nodeId);
        return saved;
    }

    @Override
    @Transactional
    public Device updateDevice(String deviceId, Device device) {
        Device existing = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在: " + deviceId));
        if (device.getDeviceName() != null) {
            existing.setDeviceName(device.getDeviceName());
        }
        if (device.getNodeId() != null) {
            existing.setNodeId(device.getNodeId());
        }
        return deviceRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteDevice(String deviceId) {
        if (!deviceRepository.existsById(deviceId)) {
            throw new RuntimeException("设备不存在: " + deviceId);
        }
        deviceRepository.deleteById(deviceId);
    }

    @Override
    public Device getDeviceById(String deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在: " + deviceId));
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public List<Device> getDevicesByProductId(String productId) {
        return deviceRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    public void updateDeviceStatus(String deviceId, String status) {
        Device device = getDeviceById(deviceId);
        device.setStatus(status);
        if ("online".equals(status)) {
            device.setLastOnlineTime(LocalDateTime.now());
        }
        deviceRepository.save(device);
        log.info("设备状态更新: deviceId={}, status={}", deviceId, status);

        // 通过WebSocket推送设备状态变更到前端
        try {
            webSocketService.sendDeviceStatusChange(device);
        } catch (Exception e) {
            log.warn("WebSocket推送设备状态失败: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void recordConnectEvent(String deviceId, String eventType) {
        DeviceConnectLog log = new DeviceConnectLog();
        log.setDeviceId(deviceId);
        log.setEventType(eventType);
        connectLogRepository.save(log);
    }

    @Override
    public List<DeviceConnectLog> getConnectLogs(String deviceId) {
        return connectLogRepository.findByDeviceIdOrderByEventTimeDesc(deviceId);
    }

    @Override
    public List<DeviceConnectLog> getRecentConnectLogs() {
        return connectLogRepository.findTop20ByOrderByEventTimeDesc();
    }

    @Override
    @Transactional
    public CommandLog sendCommand(String deviceId, String commandName, String commandParams) {
        Device device = deviceRepository.findByIdWithProduct(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在: " + deviceId));

        CommandLog commandLog = new CommandLog();
        commandLog.setDeviceId(deviceId);
        commandLog.setCommandName(commandName);
        commandLog.setCommandParams(commandParams);
        commandLog.setCommandId(generateId("CMD"));
        commandLog.setStatus("pending");

        CommandLog saved = commandLogRepository.save(commandLog);

        // 通过MQTT下发命令
        String topic = String.format("iot/%s/%s/command/%s",
                device.getProduct().getId(), deviceId, saved.getCommandId());
        mqttService.publish(topic, commandParams);

        log.info("命令下发: deviceId={}, commandName={}, commandId={}", deviceId, commandName, saved.getCommandId());
        return saved;
    }

    @Override
    @Transactional
    public void updateCommandResult(String commandId, String status, String result) {
        CommandLog commandLog = commandLogRepository.findByCommandId(commandId);
        if (commandLog != null) {
            commandLog.setStatus(status);
            commandLog.setResult(result);
            commandLog.setResponseTime(LocalDateTime.now());
            commandLogRepository.save(commandLog);
        }
    }

    @Override
    public List<CommandLog> getCommandLogs(String deviceId) {
        return commandLogRepository.findByDeviceIdOrderByRequestTimeDesc(deviceId);
    }

    @Override
    public long countOnlineDevices() {
        return deviceRepository.countByStatus("online");
    }

    @Override
    public long countTotalDevices() {
        return deviceRepository.count();
    }

    /**
     * 生成设备ID
     */
    private String generateId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 生成设备密钥
     */
    private String generateSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
