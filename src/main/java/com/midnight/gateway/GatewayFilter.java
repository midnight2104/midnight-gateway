package com.midnight.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * gateway filter.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/5/29 下午9:30
 */
public interface GatewayFilter {

    Mono<Void> filter(ServerWebExchange exchange);
}
