package com.midnight.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 后置过滤器
 */
@Component
@Slf4j
public class GatewayPostFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange).doFinally(
                s -> {
                    log.info("midnight gateway post filter");
                    exchange.getAttributes().forEach((k, v) -> log.info(k + " : " + v));
                });
    }
}
