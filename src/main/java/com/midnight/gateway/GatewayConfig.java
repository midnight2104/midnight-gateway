package com.midnight.gateway;

import com.midnight.rpc.core.api.RegistryCenter;
import com.midnight.rpc.core.registry.mid.MidnightRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RegistryCenter rc() {
        return new MidnightRegistryCenter();
    }
}
