package com.iot.platform.mqtt;

import com.iot.platform.entity.Device;
import com.iot.platform.repository.DeviceRepository;
import io.moquette.broker.Server;
import io.moquette.broker.config.MemoryConfig;
import io.moquette.broker.security.IAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

/**
 * Moquette MQTT Broker配置 - 嵌入式Broker
 */
@Slf4j
@Configuration
public class MqttBrokerConfig {

    @Autowired
    private MqttAuthHandler mqttAuthHandler;

    @Autowired
    private DeviceRepository deviceRepository;

    @Bean(destroyMethod = "stopServer")
    public Server mqttBroker() throws IOException {
        Properties props = new Properties();
        props.put("host", "0.0.0.0");
        props.put("port", "1883");
        props.put("websocket_port", "8083");
        props.put("websocket_path", "/mqtt");

        MemoryConfig config = new MemoryConfig(props);
        Server server = new Server();

        // 设备认证：username=deviceId, password=deviceSecret
        IAuthenticator authenticator = (clientId, username, password) -> {
            String deviceId = (username != null && !username.isEmpty()) ? username : clientId;
            String secret = password != null ? new String(password) : "";

            try {
                Device device = deviceRepository.findByNodeId(deviceId);
                if (device == null) {
                    device = deviceRepository.findById(deviceId).orElse(null);
                }
                if (device != null && secret.equals(device.getSecret())) {
                    log.info("MQTT设备认证成功: deviceId={}", deviceId);
                    return true;
                }
                log.warn("MQTT设备认证失败: deviceId={}", deviceId);
                return false;
            } catch (Exception e) {
                log.error("MQTT认证异常: {}", e.getMessage());
                return false;
            }
        };

        server.startServer(config, Collections.singletonList(mqttAuthHandler), null, authenticator, null);

        log.info("Moquette MQTT Broker启动成功 - TCP: 0.0.0.0:1883, WebSocket: 0.0.0.0:8083/mqtt");
        return server;
    }
}
