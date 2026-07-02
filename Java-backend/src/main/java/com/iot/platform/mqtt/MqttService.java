package com.iot.platform.mqtt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.entity.DeviceEventLog;
import com.iot.platform.repository.DeviceEventLogRepository;
import com.iot.platform.service.DeviceService;
import com.iot.platform.service.TelemetryService;
import com.iot.platform.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@DependsOn("mqttBroker")
public class MqttService {

    private MqttClient client;
    private static final String BROKER_URL = "tcp://localhost:1883";
    private static final String CLIENT_ID = "iot-platform-server";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MqttTopicRouter topicRouter;

    @Autowired
    @Lazy
    private DeviceService deviceService;

    @Autowired
    @Lazy
    private TelemetryService telemetryService;

    @Autowired
    @Lazy
    private DeviceEventLogRepository eventLogRepository;

    @Autowired
    @Lazy
    private WebSocketService webSocketService;

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 5; i++) {
            try {
                client = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setAutomaticReconnect(true);
                options.setConnectionTimeout(5);
                options.setKeepAliveInterval(20);
                client.connect(options);
                log.info("MQTT客户端连接成功: {}", BROKER_URL);

                client.subscribe("iot/+/+/telemetry", (topic, message) -> routeMessage(topic, new String(message.getPayload())));
                client.subscribe("iot/+/+/event", (topic, message) -> routeMessage(topic, new String(message.getPayload())));
                client.subscribe("iot/+/+/command/response", (topic, message) -> routeMessage(topic, new String(message.getPayload())));
                return;
            } catch (MqttException e) {
                log.warn("MQTT客户端连接失败，第{}次重试... {}", i, e.getMessage());
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        }
        log.error("MQTT客户端连接失败，已重试5次");
    }

    /**
     * 通过TopicRouter统一路由消息
     */
    private void routeMessage(String topic, String payload) {
        topicRouter.match(topic).ifPresent(match -> {
            try {
                switch (match.getType()) {
                    case TELEMETRY:
                        handleTelemetry(match.getProductId(), match.getDeviceId(), payload);
                        break;
                    case EVENT:
                        handleEvent(match.getProductId(), match.getDeviceId(), payload);
                        break;
                    case COMMAND_RESPONSE:
                        handleCommandResponse(match.getDeviceId(), payload);
                        break;
                }
            } catch (Exception e) {
                log.error("处理MQTT消息失败: topic={}, error={}", topic, e.getMessage(), e);
            }
        });
    }

    private void handleTelemetry(String productId, String deviceId, String payload) {
        try {
            Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            log.info("设备上报属性: deviceId={}, data={}", deviceId, data);
            telemetryService.ingestTelemetry(productId, deviceId, data);
        } catch (Exception e) {
            log.error("处理telemetry失败: deviceId={}, error={}", deviceId, e.getMessage(), e);
        }
    }

    private void handleEvent(String productId, String deviceId, String payload) {
        try {
            Map<String, Object> eventData = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            log.info("设备事件: deviceId={}, event={}", deviceId, eventData);

            // 存储到device_event_log
            DeviceEventLog eventLog = new DeviceEventLog();
            eventLog.setDeviceId(deviceId);
            eventLog.setProductId(productId);
            eventLog.setEventIdentifier((String) eventData.getOrDefault("eventIdentifier", "unknown"));
            eventLog.setEventType((String) eventData.getOrDefault("eventType", "info"));
            eventLog.setPayload(payload);
            eventLog.setEventTime(LocalDateTime.now());
            eventLogRepository.save(eventLog);

            webSocketService.sendDeviceTelemetry(deviceId, eventData);
        } catch (Exception e) {
            log.error("处理event失败: deviceId={}, error={}", deviceId, e.getMessage(), e);
        }
    }

    private void handleCommandResponse(String deviceId, String payload) {
        try {
            Map<String, Object> responseData = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            String commandId = (String) responseData.get("commandId");
            String status = (String) responseData.get("status");
            Object result = responseData.get("result");

            log.info("命令响应: deviceId={}, commandId={}, status={}", deviceId, commandId, status);

            if (commandId != null) {
                deviceService.updateCommandResult(commandId, status != null ? status : "success",
                        result != null ? objectMapper.writeValueAsString(result) : null);
                webSocketService.sendCommandResponse(commandId, status != null ? status : "success",
                        result != null ? objectMapper.writeValueAsString(result) : null);
            }
        } catch (Exception e) {
            log.error("处理command/response失败: error={}", e.getMessage(), e);
        }
    }

    public void publish(String topic, String payload) {
        try {
            if (client != null && client.isConnected()) {
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(1);
                client.publish(topic, message);
                log.info("MQTT消息发布: topic={}", topic);
            } else {
                log.warn("MQTT客户端未连接，无法发布消息");
            }
        } catch (MqttException e) {
            log.error("MQTT消息发布失败", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                log.info("MQTT客户端断开连接");
            }
        } catch (MqttException e) {
            log.error("MQTT客户端断开失败", e);
        }
    }
}
