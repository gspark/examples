package com.shrill.redisQueue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import redis.clients.jedis.JedisPool;

public class TaskClient implements Serializable {

  private final JedisPool pool;

  private final RedisDelayQueueImpl rdq;

  private final ObjectMapper om;

  public TaskClient(JedisPool pool) {
    this.pool = pool;
    rdq = new RedisDelayQueueImpl("delay","queue","test1", 3);
    this.om = JsonCommonUtil.getMapperInstance();
  }

  public void addTask(final String id, final Serializable command, long timeout) throws IOException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream output = new ObjectOutputStream(baos);
    output.writeObject(command);
    output.close();

    Message m = new Message();
    m.setId(id);

    m.setPayload(om.writeValueAsString(baos.toByteArray()));
    m.setTimeout(timeout);

    rdq.offer(m, pool.getResource());
  }
}
