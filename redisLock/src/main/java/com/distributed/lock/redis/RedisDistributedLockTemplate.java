package com.distributed.lock.redis;

import static org.slf4j.LoggerFactory.getLogger;

import com.distributed.lock.Callback;
import com.distributed.lock.DistributedLockTemplate;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPool;

/**
 *
 */
public class RedisDistributedLockTemplate implements DistributedLockTemplate {

    private static final Logger log = getLogger(RedisDistributedLockTemplate.class);

    private final JedisPool jedisPool;

    public RedisDistributedLockTemplate(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public Object execute(String lockId, int timeout, Callback callback) {
        RedisReentrantLock distributedReentrantLock = null;
        boolean getLock = false;
        try {
            distributedReentrantLock = new RedisReentrantLock(jedisPool, lockId);
            if (distributedReentrantLock.tryLock(new Long(timeout), TimeUnit.MILLISECONDS)) {
                getLock = true;
                return callback.onGetLock();
            } else {
                return callback.onTimeout();
            }
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (getLock) {
                distributedReentrantLock.unlock();
            }
        }
        return null;
    }
}
