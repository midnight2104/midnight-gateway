package com.midnight.gateway.plugin;

import com.midnight.rpc.core.api.LoadBalancer;
import com.midnight.rpc.core.api.RegistryCenter;
import com.midnight.rpc.core.cluster.RoundRobinLoadBalancer;
import com.midnight.rpc.core.meta.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DirectPlugin extends AbstractGatewayPlugin {
    public static final String NAME = "direct";
    private String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        log.info("=======>>>>>>> [DirectPlugin] ...");

        String backend = exchange.getRequest().getQueryParams().getFirst("backend");
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("midnight.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("midnight.gw.plugin", getName());

        // 下一个插件处理
        if (backend == null || backend.isBlank()) {
            return requestBody.flatMap(x -> exchange.getResponse().writeWith(Mono.just(x)))
                    .then(chain.handle(exchange));
        }

        WebClient client = WebClient.create(backend);
        Mono<String> body = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class)
                .retrieve()
                .toEntity(String.class)
                .map(ResponseEntity::getBody);

        return body.flatMap(x -> exchange.getResponse()
                        .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))))
                .then(chain.handle(exchange));
    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(prefix);
    }


    @Override
    public String getName() {
        return NAME;
    }
}
