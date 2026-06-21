package com.iot.platform.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Drools 规则引擎配置类
 */
@Configuration
public class DroolsConfig {

    private static final KieServices kieServices = KieServices.Factory.get();
    
    // 我们将动态从数据库加载规则，而不是仅仅从classpath，
    // 但是这里可以留一个基础的KieContainer用于加载默认规则
    @Bean
    public KieContainer kieContainer() {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);
        
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        
        return kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
    }
}
