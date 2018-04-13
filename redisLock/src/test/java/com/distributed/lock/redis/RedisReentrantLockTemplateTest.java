package com.distributed.lock.redis;

import com.distributed.lock.Callback;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

/**
 *
 */
public class RedisReentrantLockTemplateTest {

    private final JedisPool jp = new JedisPool("192.168.128.61", 6379);
    private final RedisDistributedLockTemplate template = new RedisDistributedLockTemplate(jp);

    private int size = 100;
    private final CountDownLatch startCountDownLatch = new CountDownLatch(1);
    private final CountDownLatch endDownLatch = new CountDownLatch(size);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testTry() throws InterruptedException {
        doTestTry(9000);
    }

    @Test
    public void testTry1() throws InterruptedException {
        doTestTry1(45000);
    }

    private void doTestTry(final int timeout) throws InterruptedException {
        for (int i = 0; i < size; i++) {
            new Thread() {
                public void run() {
                    try {
                        startCountDownLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    final int sleepTime = ThreadLocalRandom.current().nextInt(5) * 20000;
                    template.execute("test", timeout, new Callback() {
                        public Object onGetLock() throws InterruptedException {
                            System.out.println(Thread.currentThread().getName() + ":getLock");
                            Thread.currentThread().sleep(sleepTime);
                            System.out.println(
                                Thread.currentThread().getName() + ":sleeped:" + sleepTime);
                            endDownLatch.countDown();
                            return null;
                        }

                        public Object onTimeout() throws InterruptedException {
                            System.out.println(Thread.currentThread().getName() + ":timeout");
                            endDownLatch.countDown();
                            return null;
                        }
                    });
                }
            }.start();
        }
        startCountDownLatch.countDown();
        endDownLatch.await();
    }

    private void doTestTry1(final int timeout) throws InterruptedException {
        for (int i = 0; i < size; i++) {
            new Thread() {
                public void run() {
                    final int sleepTime = ThreadLocalRandom.current().nextInt(5) * 20000;
                    template.execute("test", timeout, new Callback() {
                        public Object onGetLock() throws InterruptedException {
                            System.out.println(Thread.currentThread().getName() + ":getLock");
                            Thread.currentThread().sleep(sleepTime);
                            System.out.println(
                                Thread.currentThread().getName() + ":sleeped:" + sleepTime);
                            endDownLatch.countDown();
                            return null;
                        }

                        public Object onTimeout() throws InterruptedException {
                            System.out.println(Thread.currentThread().getName() + ":timeout");
                            endDownLatch.countDown();
                            return null;
                        }
                    });
                }
            }.start();
            Thread.sleep(10000);
        }
        endDownLatch.await();
    }

}
