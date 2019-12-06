package com.yang.gateway.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jevon
 */
@Component
@ConditionalOnProperty(
        name = {"spring.cloud.gateway.datasource"},
        havingValue = "redis",
        matchIfMissing = true
)
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {
    public static final String  GATEWAY_ROUTES = "geteway_routes";

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        redisTemplate.opsForHash().values(GATEWAY_ROUTES).stream().forEach(routeDefinition -> {
            try {
                routeDefinitions.add(mapper.readValue(routeDefinition.toString(), RouteDefinition.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return Flux.fromIterable(routeDefinitions);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route
                .flatMap(routeDefinition -> {
                    try {
                        redisTemplate.opsForHash().put(GATEWAY_ROUTES, routeDefinition.getId(),
    //                            JSON.toJSONString(routeDefinition)
                            mapper.writeValueAsString(routeDefinition)
                        );
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            if (redisTemplate.opsForHash().hasKey(GATEWAY_ROUTES, id)) {
                redisTemplate.opsForHash().delete(GATEWAY_ROUTES, id);
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
        });
    }
}
