package com.shrill.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class HelloController {

    @Value("${remote.home}")
    private URI home;

    @GetMapping("/hello")
    public Mono<String> sayHello() {
        return Mono.just("Hello World!");
    }

    @GetMapping("/test")
    public Mono<ResponseEntity<byte[]>> proxy(ProxyExchange<byte[]> proxy) throws Exception {
        return proxy.uri(home.toString() + "/image/png").get();
    }
}
