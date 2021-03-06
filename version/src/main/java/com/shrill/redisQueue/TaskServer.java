package com.shrill.redisQueue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import redis.clients.jedis.JedisPool;

public class TaskServer {

  private ThreadPoolExecutor ex;

  private volatile boolean runing;

  private JedisPool pool;

  private RedisDelayQueueImpl jdq = new RedisDelayQueueImpl("delay","queue","test1", 3);

  private final ObjectMapper om;

  public TaskServer(JedisPool pool) {
    ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(20);

    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();

    this.ex = new ThreadPoolExecutor(4, 10, 20, TimeUnit.SECONDS, workQueue, namedThreadFactory);

    this.pool = pool;

    this.om = JsonCommonUtil.getMapperInstance();

    runing = true;
  }

  public void start() {
    while (runing) {
      try {
        Message m = jdq.take(pool.getResource());

        byte[] b = om.readValue(m.getPayload(), byte[].class);
        ByteArrayInputStream in = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(in);
        Runnable r = (Runnable) ois.readObject();
        if( null != r ) {
          ex.execute(r);
        }
      } catch (InterruptedException | IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  public void stop() {
    runing = false;

    ex.shutdown();
  }


}
