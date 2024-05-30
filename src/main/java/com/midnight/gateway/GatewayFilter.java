package com.midnight.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 前置过滤器
 */
@Component
@Slf4j
public class GatewayFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("midnight gateway web filter....");

        if (exchange.getRequest().getQueryParams().getFirst("mock") == null) {
            return chain.filter(exchange);
        }

        String mock = """
                {"result": "mock"}
                """;
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(mock.getBytes(StandardCharsets.UTF_8))));
    }
}
