package com.midnight.gateway.plugin;

import com.midnight.rpc.core.api.LoadBalancer;
import com.midnight.rpc.core.api.RegistryCenter;
import com.midnight.rpc.core.cluster.RoundRobinLoadBalancer;
import com.midnight.rpc.core.meta.InstanceMeta;
import com.midnight.rpc.core.meta.ServiceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class MidnightRpcPlugin extends AbstractGatewayPlugin {
    public static final String NAME = "midnight-rpc";
    private String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    @Autowired
    RegistryCenter rc;

    LoadBalancer<InstanceMeta> loadBalancer = new RoundRobinLoadBalancer<>();


    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        log.info("=======>>>>>>> [MidnightRpcPlugin] ...");

        // 1. 获取服务名称
        String service = exchange.getRequest().getPath().value().substring(prefix.length());
        ServiceMeta serviceMeta = ServiceMeta.builder().name(service)
                .app("app1").env("dev").namespace("public").build();
        // 2. 通过rc拿到所有活着的服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);
        // 3. 负载均衡

        InstanceMeta instanceMeta = loadBalancer.choose(instanceMetas);
        log.info(" inst size=" + instanceMetas.size() + ", inst  " + instanceMeta);
        String url = instanceMeta.toUrl();

        // 4. 拿到请求的报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        // 5. 通过webclient发送post请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);
        // 6. 通过entity获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
        // body.subscribe(souce -> System.out.println("response:" + souce));
        // 7. 组装响应报文
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("midnight.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("midnight.gw.plugin", getName());
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
