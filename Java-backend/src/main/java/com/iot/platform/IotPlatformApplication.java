package com.iot.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 物联网平台主启动类
 */
@SpringBootApplication
@EnableScheduling
public class IotPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotPlatformApplication.class, args);
        System.out.println("============================================");
        System.out.println("  IoT Platform 启动成功！");
        System.out.println("  API文档: http://localhost:8080/api/swagger-ui/");
        System.out.println("  MQTT Broker: tcp://localhost:1883");
        System.out.println("  MQTT WebSocket: ws://localhost:8083/mqtt");
        System.out.println("============================================");
    }
}
