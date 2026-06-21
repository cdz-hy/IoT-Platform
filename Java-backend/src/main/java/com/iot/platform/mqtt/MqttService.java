package com.iot.platform.mqtt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.service.AlertService;
import com.iot.platform.service.DeviceService;
import com.iot.platform.service.DeviceShadowService;
import com.iot.platform.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * MQTT客户端服务 - 用于平台向设备发布消息，同时接收设备上报数据
 */
@Slf4j
@Service
public class MqttService {

    private MqttClient client;
    private static final String BROKER_URL = "tcp://localhost:1883";
    private static final String CLIENT_ID = "iot-platform-server";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Lazy
    private DeviceService deviceService;

    @Autowired
    @Lazy
    private DeviceShadowService shadowService;

    @Autowired
    @Lazy
    private AlertService alertService;

    @Autowired
    @Lazy
    private WebSocketService webSocketService;

    @PostConstruct
    public void init() {
        try {
            client = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);

            client.connect(options);
            log.info("MQTT客户端连接成功: {}", BROKER_URL);

            // 订阅命令响应主题
            client.subscribe("iot/+/+/command/response", (topic, message) -> {
                log.info("收到命令响应: topic={}, payload={}", topic, new String(message.getPayload()));
                handleCommandResponse(topic, new String(message.getPayload()));
            });

            // 订阅设备上报属性主题
            client.subscribe("iot/+/+/telemetry", (topic, message) -> {
                log.info("收到设备上报数据: topic={}, payload={}", topic, new String(message.getPayload()));
                handleTelemetry(topic, new String(message.getPayload()));
            });

            // 订阅设备事件主题
            client.subscribe("iot/+/+/event", (topic, message) -> {
                log.info("收到设备事件: topic={}, payload={}", topic, new String(message.getPayload()));
                handleEvent(topic, new String(message.getPayload()));
            });

        } catch (MqttException e) {
            log.error("MQTT客户端连接失败", e);
        }
    }

    /**
     * 发布MQTT消息
     */
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

    /**
     * 处理设备上报的属性数据
     * 主题格式: iot/{productId}/{deviceId}/telemetry
     */
    private void handleTelemetry(String topic, String payload) {
        try {
            String[] parts = topic.split("/");
            if (parts.length < 4) {
                log.warn("telemetry主题格式错误: {}", topic);
                return;
            }
            String productId = parts[1];
            String deviceId = parts[2];

            // 解析上报数据
            Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            log.info("设备上报属性: deviceId={}, data={}", deviceId, data);

            // 1. 更新设备影子reported区
            shadowService.updateReported(deviceId, data);

            // 2. 逐项检查告警规则
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                alertService.checkAlert(deviceId, productId, entry.getKey(), entry.getValue());
            }

            // 3. 通过WebSocket推送到前端
            webSocketService.sendDeviceTelemetry(deviceId, data);

        } catch (Exception e) {
            log.error("处理telemetry消息失败: topic={}, error={}", topic, e.getMessage(), e);
        }
    }

    /**
     * 处理设备事件
     * 主题格式: iot/{productId}/{deviceId}/event
     */
    private void handleEvent(String topic, String payload) {
        try {
            String[] parts = topic.split("/");
            if (parts.length < 4) {
                log.warn("event主题格式错误: {}", topic);
                return;
            }
            String deviceId = parts[2];

            Map<String, Object> eventData = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            log.info("设备事件上报: deviceId={}, event={}", deviceId, eventData);

            // 通过WebSocket推送事件到前端
            webSocketService.sendDeviceTelemetry(deviceId, eventData);

        } catch (Exception e) {
            log.error("处理event消息失败: topic={}, error={}", topic, e.getMessage(), e);
        }
    }

    /**
     * 处理命令响应
     * 主题格式: iot/{productId}/{deviceId}/command/response
     */
    private void handleCommandResponse(String topic, String payload) {
        try {
            String[] parts = topic.split("/");
            // iot/{productId}/{deviceId}/command/response → parts[0]=iot, [1]=productId, [2]=deviceId, [3]=command, [4]=response
            if (parts.length < 5) {
                log.warn("command/response主题格式错误: {}", topic);
                return;
            }
            String deviceId = parts[2];

            Map<String, Object> responseData = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
            String commandId = (String) responseData.get("commandId");
            String status = (String) responseData.get("status");
            String result = (String) responseData.get("result");

            log.info("命令响应: deviceId={}, commandId={}, status={}", deviceId, commandId, status);

            // 更新命令记录状态
            if (commandId != null) {
                deviceService.updateCommandResult(commandId, status != null ? status : "success", result);

                // 通过WebSocket推送命令响应到前端
                webSocketService.sendCommandResponse(commandId, status != null ? status : "success", result);
            }

        } catch (Exception e) {
            log.error("处理command/response消息失败: topic={}, error={}", topic, e.getMessage(), e);
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
