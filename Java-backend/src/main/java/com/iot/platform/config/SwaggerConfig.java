package com.iot.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 3 API文档配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IoT Platform API")
                        .description("物联网平台接口文档 - 基于Spring Boot + MQTT")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("IoT Platform")
                                .url("http://localhost:8080/api")));
    }
}
