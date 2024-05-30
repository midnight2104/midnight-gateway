package com.midnight.gateway;

import com.midnight.rpc.core.api.RegistryCenter;
import com.midnight.rpc.core.registry.mid.MidnightRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

@Configuration
public class GatewayConfig {

    @Bean
    public RegistryCenter rc() {
        return new MidnightRegistryCenter();
    }

    @Bean
    public ApplicationRunner runner(@Autowired ApplicationContext context) {
        return args -> {
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);

            Properties mappings = new Properties();
            mappings.put("/gw/**", "gatewayWebHandler");

            handlerMapping.setMappings(mappings);
            handlerMapping.initApplicationContext();
        };
    }
}
