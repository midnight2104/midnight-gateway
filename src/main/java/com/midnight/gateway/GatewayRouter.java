package com.midnight.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class GatewayRouter {

    @Autowired
    private GatewayHandler gatewayHandler;

    @Bean
    public RouterFunction<?> gatewayRouterFunction(){
            return route(GET("/gw").or(POST("/gw/**")), gatewayHandler::handle);
    }
}
