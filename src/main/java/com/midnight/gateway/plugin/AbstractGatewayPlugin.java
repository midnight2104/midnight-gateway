package com.midnight.gateway.plugin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractGatewayPlugin implements GatewayPlugin {

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean support(ServerWebExchange exchange) {
        return doSupport(exchange);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain) {
        boolean supported = support(exchange);
        log.info(" =====>>>> plugin[" + this.getName() + "], support=" + supported);

        return supported ? doHandle(exchange, chain) : chain.handle(exchange);
    }


    public abstract Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain);

    public abstract boolean doSupport(ServerWebExchange exchange);

}
