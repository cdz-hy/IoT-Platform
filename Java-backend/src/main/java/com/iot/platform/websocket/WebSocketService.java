package com.iot.platform.websocket;

import com.iot.platform.entity.AlertRecord;
import com.iot.platform.entity.Device;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * WebSocket推送服务 - 向前端实时推送数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 推送设备状态变更
     */
    public void sendDeviceStatusChange(Device device) {
        Map<String, Object> payload = Map.of(
                "deviceId", device.getDeviceId(),
                "deviceName", device.getDeviceName(),
                "status", device.getStatus(),
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/device/status", payload);
        log.debug("推送设备状态变更: deviceId={}, status={}", device.getDeviceId(), device.getStatus());
    }

    /**
     * 推送设备最新数据
     */
    public void sendDeviceTelemetry(String deviceId, Map<String, Object> data) {
        Map<String, Object> payload = Map.of(
                "deviceId", deviceId,
                "data", data,
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/device/telemetry", payload);
        log.debug("推送设备数据: deviceId={}", deviceId);
    }

    /**
     * 推送告警通知
     */
    public void sendAlertNotification(AlertRecord alert) {
        Map<String, Object> payload = Map.of(
                "alertId", alert.getId(),
                "deviceId", alert.getDeviceId(),
                "content", alert.getAlertContent(),
                "status", alert.getStatus(),
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/alert", payload);
        log.info("推送告警通知: alertId={}", alert.getId());
    }

    /**
     * 推送命令响应
     */
    public void sendCommandResponse(String commandId, String status, String result) {
        Map<String, Object> payload = Map.of(
                "commandId", commandId,
                "status", status,
                "result", result != null ? result : "",
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/command/response", payload);
    }
}
