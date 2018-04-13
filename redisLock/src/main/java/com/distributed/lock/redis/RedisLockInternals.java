package com.distributed.lock.redis;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *
 */
class RedisLockInternals {

    private static final Logger log = getLogger(RedisLockInternals.class);
    private final JedisPool jedisPool;

    /**
     * 重试等待时间 millisecond
     */
    private int retryAwait = 200;

    /**
     * redis key 过期时间  单位 millisecond, 3 minutes
     */
    private int expire = 3 * 60 * 1000;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture sfs;

    RedisLockInternals(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    String tryRedisLock(String lockId, long time, TimeUnit unit) {
        final long startMillis = System.currentTimeMillis();
        final Long millisToWait = (unit != null) ? unit.toMillis(time) : null;
        String lockValue;
        do {
            lockValue = createRedisKey(lockId);
            if (lockValue != null) {
                break;
            }
            if (System.currentTimeMillis() - startMillis - retryAwait > millisToWait) {
                break;
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(retryAwait));

        } while (lockValue == null);

        if (null != lockValue) {
            log.info("lockValue:{}", lockValue);
            startPuttingOnExpire(lockId);
        }
        return lockValue;
    }

    private String createRedisKey(String lockId) {
        Jedis jedis = null;
        try {
            String value = lockId + randomId(2);
            jedis = jedisPool.getResource();
            String luaScript = ""
                + "\nlocal r = tonumber(redis.call('SETNX', KEYS[1],ARGV[1]));"
                + "\nif r == 1 then"
                + "\nredis.call('PEXPIRE',KEYS[1],ARGV[2]);"
                + "\nend"
                + "\nreturn r";
            List<String> keys = new ArrayList<String>();
            keys.add(lockId);
            List<String> args = new ArrayList<String>();
            args.add(value);
            args.add(expire + "");
            Long ret = (Long) jedis.eval(luaScript, keys, args);
            if (new Long(1).equals(ret)) {
                return value;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    private void startPuttingOnExpire(final String key) {
        sfs = scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Jedis jedis = null;
                try {
                    jedis = jedisPool.getResource();
                    String luaScript = ""
                        + "\nlocal r = redis.call('PEXPIRE',KEYS[1],ARGV[1]);"
                        + "\nreturn r";
                    List<String> keys = new ArrayList<String>();
                    keys.add(key);
                    List<String> args = new ArrayList<String>();
                    args.add(expire + "");
                    jedis.eval(luaScript, keys, args);
                } finally {
                    if (jedis != null) {
                        jedis.close();
                    }
                }
            }
        }, 0, expire / 3, TimeUnit.MILLISECONDS);
    }

    void unlockRedisLock(String key, String value) {
        if (null != sfs && !sfs.isCancelled()) {
            sfs.cancel(true);
            scheduler.shutdown();
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String luaScript = ""
                + "\nlocal v = redis.call('GET', KEYS[1]);"
                + "\nlocal r= 0;"
                + "\nif v == ARGV[1] then"
                + "\nr =redis.call('DEL',KEYS[1]);"
                + "\nend"
                + "\nreturn r";
            List<String> keys = new ArrayList<String>();
            keys.add(key);
            List<String> args = new ArrayList<String>();
            args.add(value);
            jedis.eval(luaScript, keys, args);
            log.info("unlock value:{}", value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    private final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
        '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
        'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
        'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
        'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
        'Z'};

    private String randomId(int size) {
        char[] cs = new char[size];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = digits[ThreadLocalRandom.current().nextInt(digits.length)];
        }
        return new String(cs);
    }
}
