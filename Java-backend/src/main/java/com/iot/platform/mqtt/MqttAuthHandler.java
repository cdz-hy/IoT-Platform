package com.iot.platform.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.platform.entity.Device;
import com.iot.platform.service.DeviceService;
import com.iot.platform.service.DeviceShadowService;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * MQTT认证和事件拦截处理器
 * 处理设备连接、断开、消息发布等事件
 */
@Slf4j
@Component
public class MqttAuthHandler extends AbstractInterceptHandler {

    @Autowired
    @Lazy
    private DeviceService deviceService;

    @Autowired
    @Lazy
    private DeviceShadowService deviceShadowService;

    @Autowired
    @Lazy
    private MqttService mqttService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String getID() {
        return "MqttAuthHandler";
    }

    @Override
    public void onSessionLoopError(Throwable error) {
        log.error("MQTT会话循环错误: {}", error.getMessage());
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {
        String clientId = msg.getClientID();
        String username = msg.getUsername();
        log.info("MQTT客户端连接: clientId={}, username={}", clientId, username);

        try {
            String deviceId = (username != null && !username.isEmpty()) ? username : clientId;
            deviceService.updateDeviceStatus(deviceId, "online");
            deviceService.recordConnectEvent(deviceId, "online");

            // 自动同步云端影子 Delta 给设备
            Device device = deviceService.getDeviceById(deviceId);
            if (device != null && device.getProduct() != null) {
                String productId = device.getProduct().getId();
                Map<String, Object> delta = deviceShadowService.syncDeltaOnConnect(deviceId);
                if (delta != null && !delta.isEmpty()) {
                    String deltaTopic = String.format("iot/%s/%s/shadow/delta", productId, deviceId);
                    String payload = objectMapper.writeValueAsString(delta);
                    mqttService.publish(deltaTopic, payload);
                    log.info("设备上线触发影子Delta下发: topic={}, delta={}", deltaTopic, payload);
                }
            }
        } catch (Exception e) {
            log.warn("处理设备上线事件失败: clientId={}, error={}", clientId, e.getMessage());
        }
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
        String clientId = msg.getClientID();
        log.info("MQTT客户端断开: clientId={}", clientId);

        try {
            deviceService.updateDeviceStatus(clientId, "offline");
            deviceService.recordConnectEvent(clientId, "offline");
        } catch (Exception e) {
            log.warn("更新设备离线状态失败: clientId={}, error={}", clientId, e.getMessage());
        }
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
        String topic = msg.getTopicName();
        String payload = new String(msg.getPayload().array());
        log.info("MQTT消息发布: topic={}, payload={}", topic, payload);
    }

    @Override
    public void onSubscribe(InterceptSubscribeMessage msg) {
        String clientId = msg.getClientID();
        String topic = msg.getTopicFilter();
        log.info("MQTT订阅: clientId={}, topic={}", clientId, topic);
    }

    @Override
    public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
        String clientId = msg.getClientID();
        String topic = msg.getTopicFilter();
        log.info("MQTT取消订阅: clientId={}, topic={}", clientId, topic);
    }
}
