package com.midnight.gateway.filter;

import com.midnight.gateway.GatewayFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@Component("demoFilter")
public class DemoFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        log.info(" ===>>> filters: demo filter ...");
        exchange.getRequest().getHeaders().toSingleValueMap()
                .forEach((k, v) -> log.info(k + ":" + v));
        return Mono.empty();
    }
}
