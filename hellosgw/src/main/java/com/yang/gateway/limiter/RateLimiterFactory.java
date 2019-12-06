package com.yang.gateway.limiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jevon
 */

@Component
public class RateLimiterFactory {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    @Qualifier("redisLockRegistry")
    private RedisLockRegistry redisLockRegistry;

    /**
     * 本地持有对象
     */
    private volatile Map<String, RedisRateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    /**
     * @param key              redis key
     * @param permitsPerSecond 每秒产生的令牌数
     * @param maxBurstSeconds  最大存储多少秒的令牌
     * @return
     */
    public RedisRateLimiter build(String key, long permitsPerSecond, long maxBurstSeconds) {
        if (!rateLimiterMap.containsKey(key)) {
            synchronized (this) {
                if (!rateLimiterMap.containsKey(key)) {

                    String requestId = UUID.randomUUID().toString();
                    rateLimiterMap.put(key,
                        new RedisRateLimiter(key, maxBurstSeconds, permitsPerSecond, redisLockRegistry.obtain("lock"),
                            redisTemplate));
                }
            }
        }
        return rateLimiterMap.get(key);
    }
}
