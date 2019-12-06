package com.yang.gateway.limiter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author jevon
 */
public class RedisRateLimiter {

    private static Logger logger = LoggerFactory.getLogger(RedisRateLimiter.class);

    private StringRedisTemplate redisTemplate;

    private String key;

    private long maxPermits;

    private long rate;

    private Lock redisLocker;

    private final ObjectMapper mapper = new ObjectMapper();

    public RedisRateLimiter(String key, long maxPermits, long rate, Lock redisLocker, StringRedisTemplate redisTemplate) {
        this.key = key;
        this.maxPermits = maxPermits;
        this.rate = rate;
        this.redisLocker = redisLocker;
        this.redisTemplate = redisTemplate;
    }

    private void setPermits(String sKey, PermitsEntity.Permits permits) {
        // long ttl = (permits.getMaxPermits() / permits.getRate()) * 2;
        redisTemplate.opsForValue().set("RateLimiter:" + sKey, permits.toByteArray().toString());
    }

    private void setPermits(String sKey, Permits permits) {
        // long ttl = (permits.getMaxPermits() / permits.getRate()) * 2;

        try {
            redisTemplate.opsForValue().set("RateLimiter:" + sKey, mapper.writeValueAsString(permits));
        } catch (JsonProcessingException e) {
            logger.error("", e);
        }
    }

    private PermitsEntity.Permits getPermits(String sKey) {
        try {
            String data = redisTemplate.opsForValue().get("RateLimiter:" + sKey);
            if (data != null) {
                return PermitsEntity.Permits.parseFrom(data.getBytes());
            }
            return null;
        } catch (InvalidProtocolBufferException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private Permits getPermit(String sKey) {
        try {
            return mapper.readValue(redisTemplate.opsForValue().get("RateLimiter:" + sKey), Permits.class);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private PermitsEntity.Permits getPermit() {

        try {
            this.redisLocker.tryLock(500, TimeUnit.MILLISECONDS);
            PermitsEntity.Permits permits = this.getPermits(this.key);
            if (permits == null) {
                PermitsEntity.Permits.Builder builder = PermitsEntity.Permits.newBuilder();
                builder.setMaxPermits(this.maxPermits);
                builder.setRate(this.rate);
                builder.setCurrentPermits(this.maxPermits);
                builder.setLastMilliSecond(System.currentTimeMillis());
                permits = builder.build();
            }
            return permits;
        } catch (InterruptedException e) {
            logger.error("获取锁失败={}", Throwables.getStackTraceAsString(e));
        } finally {
            this.redisLocker.unlock();
        }
        return null;
    }

    private Permits getDefaultPermits() {
        Permits permits = this.getPermit(this.key);
        try {
            if (permits == null) {
                return new Permits(this.maxPermits, this.maxPermits, this.rate, System.currentTimeMillis());
            }
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
        }
        return permits;
    }


    /**
     * 用json序列化的逻辑
     * @param queryPermits
     * @return
     */
    private boolean tryAcquire(long queryPermits) {
        try {
            Permits permits = this.getDefaultPermits();
            long fillTokens = (System.currentTimeMillis() - permits.getLastMilliSecond()) / (permits.getRate() * 1000);
            long currentPermits = permits.getCurrentPermits();
            if (fillTokens >= 1) {
                currentPermits = Math.min(permits.getMaxPermits(), currentPermits + fillTokens);
            }
            if (currentPermits - queryPermits >= 0) {
                permits.setCurrentPermits(currentPermits - queryPermits);
                permits.setLastMilliSecond(System.currentTimeMillis());
                this.setPermits(key, permits);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 用protobuf序列化的逻辑
     * @param queryPermits
     * @return
     */
    private boolean tryOneAcquire(long queryPermits) {
        try {
            PermitsEntity.Permits permits = this.getPermit();
            long fillTokens = (System.currentTimeMillis() - permits.getLastMilliSecond()) / (permits.getRate() * 1000);
            long currentPermits = permits.getCurrentPermits();
            if (fillTokens >= 1) {
                currentPermits = Math.min(permits.getMaxPermits(), currentPermits + fillTokens);
            }
            if (currentPermits - queryPermits >= 0) {
                PermitsEntity.Permits.Builder builder = permits.toBuilder();
                builder.setCurrentPermits(currentPermits - queryPermits);
                builder.setLastMilliSecond(System.currentTimeMillis());
                this.setPermits(key, builder.build());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean tryAcquire() {
        return this.tryOneAcquire(1L);
    }

}
