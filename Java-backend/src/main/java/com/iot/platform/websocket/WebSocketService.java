package com.iot.platform.websocket;

import com.iot.platform.entity.AlertRecord;
import com.iot.platform.entity.Device;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendDeviceStatusChange(Device device) {
        Map<String, Object> payload = Map.of(
                "deviceId", device.getDeviceId(),
                "deviceName", device.getDeviceName(),
                "status", device.getStatus(),
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/device/status", payload);
    }

    public void sendDeviceTelemetry(String deviceId, Map<String, Object> data) {
        Map<String, Object> payload = Map.of(
                "deviceId", deviceId,
                "data", data,
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/device/telemetry", payload);
    }

    public void sendAlertNotification(AlertRecord alert) {
        Map<String, Object> payload = Map.of(
                "alertId", alert.getId(),
                "deviceId", alert.getDeviceId(),
                "productId", alert.getProductId(),
                "propertyIdentifier", alert.getPropertyIdentifier(),
                "actualValue", alert.getActualValue(),
                "content", alert.getAlertContent(),
                "status", alert.getStatus(),
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/alert", payload);
    }

    public void sendCommandResponse(String commandId, String status, String result) {
        Map<String, Object> payload = Map.of(
                "commandId", commandId,
                "status", status,
                "result", result != null ? result : "",
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/command/response", payload);
    }

    public void sendShadowUpdate(String deviceId, Map<String, Object> shadowData) {
        Map<String, Object> payload = Map.of(
                "deviceId", deviceId,
                "shadow", shadowData,
                "timestamp", System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/shadow", payload);
    }
}
