package com.yang.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FallBackController {

    @RequestMapping(value = "/fallback")
    public Mono<String> fallBack() {
        return Mono.just("连接超时，请重试……");
    }

    @RequestMapping(value = "/fallback1", method = RequestMethod.GET)
    public Flux<String> fallBack1() {
        return Flux.just("Hello", "World");
    }

    @RequestMapping(value = "/fallback2", method = RequestMethod.POST)
    public Flux<String> fallBack2() {
        return Flux.just("Hello", "World", "2");
    }
}
