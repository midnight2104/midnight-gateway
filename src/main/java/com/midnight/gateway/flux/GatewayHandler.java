package com.midnight.gateway.flux;

import com.midnight.rpc.core.api.LoadBalancer;
import com.midnight.rpc.core.api.RegistryCenter;
import com.midnight.rpc.core.cluster.RoundRobinLoadBalancer;
import com.midnight.rpc.core.meta.InstanceMeta;
import com.midnight.rpc.core.meta.ServiceMeta;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

//@Component
public class GatewayHandler {

//    @Autowired
    private RegistryCenter rc;

    private LoadBalancer<InstanceMeta> loadBalancer = new RoundRobinLoadBalancer();

    @NotNull
    public Mono<ServerResponse> handle(ServerRequest request) {
        // 1.通过请求路径获取服务名称
        String service = request.path().substring(4);
        ServiceMeta serviceMeta = ServiceMeta.builder().env("dev").name(service)
                .namespace("public").app("app1").build();

        // 2.通过rc拿到所有或者的实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);

        // 3.负载均衡
        InstanceMeta instanceMeta = loadBalancer.choose(instanceMetas);

        // 4.拿到请求报文
        Mono<String> reqMono = request.bodyToMono(String.class);

        return reqMono.flatMap(x -> invokeFromRegistry(x, instanceMeta.toURL()));
    }

    private Mono<ServerResponse> invokeFromRegistry(String x, String url) {
        // 5.通过webclient发送post请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .bodyValue(x)
                .retrieve()
                .toEntity(String.class);

        // 6.通过entity获取响应报文
        Mono<Object> body = entity.map(ResponseEntity::getBody);

        // 7. 组装响应报文
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("midnight.gw.version", "v1.0.0")
                .body(body, String.class);

    }
}
