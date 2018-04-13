package com.shrill;

import com.shrill.redisQueue.RedisDelayQueueImpl;
import com.shrill.redisQueue.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisQueueTest {

  private static JedisPool pool;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    JedisPoolConfig config = new JedisPoolConfig();
    config.setTestOnBorrow(true);
    config.setTestOnCreate(true);
    config.setMaxTotal(10);
    config.setMaxIdle(5);
    config.setMaxWaitMillis(60_000);
    pool = new JedisPool(config, "192.168.31.242", 6379, 2000, "Qwe123");
  }


  @Test
  public void testBlocking() {
    RedisDelayQueueImpl jdq = new RedisDelayQueueImpl("delay","queue","test1", 3);

    Message m = new Message();
    m.setId("a12345");
    m.setPayload("a12345");
    m.setTimeout(100);

    Message m1 = new Message();
    m1.setId("a123456");
    m1.setPayload("a123456");
    m1.setTimeout(200);

    try {
      jdq.offer(m, pool.getResource());
      jdq.offer(m1, pool.getResource());
      Message m2 = null;
      do {
        m2 = jdq.take(pool.getResource());
      } while( null == m2);
      System.out.println(m2.toString());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void testBlocking1() {
    RedisDelayQueueImpl jdq = new RedisDelayQueueImpl("delay","queue","test1", 3);

    Message m = new Message();
    m.setId("a12345789");
    m.setPayload("a12345789");
    m.setTimeout(1000);

    Message m1 = new Message();
    m1.setId("a123456");
    m1.setPayload("a123456");
    m1.setTimeout(2000);

    Message m2 = new Message();
    m2.setId("a1234567");
    m2.setPayload("a1234567");
    m2.setTimeout(8000);

    Message m3 = new Message();
    m3.setId("a12345678");
    m3.setPayload("a12345678");
    m3.setTimeout(4000);

    List<Message> l = new ArrayList<>(3);
    l.add(m);
    l.add(m1);
    l.add(m2);
    l.add(m3);
    try {
      jdq.offer(l, pool.getResource());
      Message mt = null;
      do {
        mt = jdq.take(pool.getResource());
      } while( null == mt);
      System.out.println(mt.toString());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testRemove() {
    RedisDelayQueueImpl jdq = new RedisDelayQueueImpl("delay","queue","test1", 3);
    jdq.remove("a123456", pool.getResource());
  }


  @Test
  public void testRemove1() {
    RedisDelayQueueImpl jdq = new RedisDelayQueueImpl("delay","queue","test1", 3);
    List<String> l = new ArrayList<>(2);
    l.add("a12345678");
    l.add("a1234567");
    jdq.remove(l, pool.getResource());
  }

}
