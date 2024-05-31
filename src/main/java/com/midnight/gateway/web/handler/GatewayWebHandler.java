package com.midnight.gateway.web.handler;

import com.midnight.gateway.GatewayFilter;
import com.midnight.gateway.plugin.DefaultGatewayPluginChain;
import com.midnight.gateway.plugin.GatewayPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component("gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {
    @Autowired
    List<GatewayPlugin> plugins;

    @Autowired
    List<GatewayFilter> filters;


    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        log.info(" ====> Midnight Gateway web handler ... ");

        if (plugins == null || plugins.isEmpty()) {
            String mock = """
                    {"result":"no plugin"}
                    """;

            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
        }

        for (GatewayFilter filter : filters) {
            filter.filter(exchange);
        }

        return new DefaultGatewayPluginChain(plugins).handle(exchange);
    }
}
