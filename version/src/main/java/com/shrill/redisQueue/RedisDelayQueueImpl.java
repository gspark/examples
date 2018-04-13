package com.shrill.redisQueue;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Tuple;


/**
 * 基于redis的延迟队列，采用jackson实现对象序列化
 *
 */
public class RedisDelayQueueImpl implements RedisDelayQueue {

    private final String queueShard;

    private final String messageStoreKey;

    private final int dbIndex;

    private final ObjectMapper om;

    private final transient ReentrantLock lock = new ReentrantLock();

    private final Condition available = lock.newCondition();

    private Thread leader = null;

    public final static int TWO_HOUR = 2 * 60 * 60 * 1000;

    public RedisDelayQueueImpl(String redisKeyPrefix, String queueName, String shardName) {
        this(redisKeyPrefix, queueName, shardName, 0);
    }


    /**
     * @param redisKeyPrefix
     * @param queueName
     * @param shardName 可以采用workid，不同的app，workid会不一样，这样就没有多个app重复消费的问题。
     * @param dbIndex
     */
    public RedisDelayQueueImpl(String redisKeyPrefix, String queueName, String shardName, int dbIndex) {
        this.messageStoreKey = redisKeyPrefix + ".MESSAGE." + queueName;
        this.queueShard = redisKeyPrefix + ".QUEUE." + queueName + "." + shardName;
        this.dbIndex = dbIndex;

        this.om = JsonCommonUtil.getMapperInstance();
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.jhqc.pxsj.msa.pub.delaywork.RedisDelayQueue#remove(java.lang.String,
     * redis.clients.jedis.Jedis)
     */
    @Override
    public boolean remove(String messageId, Jedis connect) {
        try {
            if (dbIndex != 0) {
                connect.select(dbIndex);
            }

            Long removed = connect.zrem(queueShard, messageId);
            Long msgRemoved = connect.hdel(messageStoreKey, messageId);

            if (removed > 0 && msgRemoved > 0) {
                return true;
            }
            return false;
        } finally {
            connect.close();
        }
    }

    @Override public void remove(final List<String> messageIds, Jedis connect) {
        try {
            if (dbIndex != 0) {
                connect.select(dbIndex);
            }
            Pipeline pipe = connect.pipelined();
            List<Response<Long>> responses = new LinkedList<>();
            for(String id : messageIds) {
                responses.add(pipe.zrem(queueShard, id));
            }
            pipe.clear();

            for(int i = 0; i < messageIds.size(); i++) {
                Long removed = responses.get(i).get();
                if (removed > 0) {
                    pipe.hdel(messageStoreKey, messageIds.get(i));
                }
            }
            pipe.clear();
        } finally {
            connect.close();
        }
    }

    /**
     * 线程安全，阻塞的消息入队
     * 
     * @param message
     * @param connect
     * @return
     * @throws IOException
     */
    public boolean offer(Message message, Jedis connect) throws IOException {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (dbIndex != 0) {
                connect.select(dbIndex);
            }
            push(message, connect);
            Message msg = peek(connect);
            if (null != msg && msg.equals(message)) {
                leader = null;
                available.signal();
            }
            return true;
        } finally {
            connect.close();
            lock.unlock();
        }
    }

    @Override
    public boolean offer(List<Message> messages, Jedis connect) throws IOException {
        if( messages.isEmpty() ) {
            return true;
        }

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (dbIndex != 0) {
                connect.select(dbIndex);
            }
            int idx = push(messages, connect);
            Message msg = peek(connect);
            if (null != msg && msg.equals(messages.get(idx))) {
                leader = null;
                available.signal();
            }
            return true;
        } finally {
            connect.close();
            lock.unlock();
        }
    }

    /**
     * 阻塞消息出队，直到有到期的消息
     *
     * @param connect
     * @return Message 如果null，数据有错误
     * @throws InterruptedException
     * @throws IOException
     */
    public Message take(Jedis connect) throws InterruptedException, IOException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();

        try {
            if (dbIndex != 0) {
                connect.select(dbIndex);
            }

            for (;;) {
                Tuple first = doPeek(0, connect);

                if (null == first) {
                    // 队列没有数据或者没到期 等待200毫秒
                    System.out.println("null");
                    available.await(200, TimeUnit.MILLISECONDS);
                } else {
                    double delay = first.getScore() - System.currentTimeMillis();
                    if (delay <= 0) {
                        // 到期
                        return takeMessage(first.getElement(), connect);
                    }
                    first = null;
                    if (leader != null) {
                        available.await();
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            available.await((long)delay, TimeUnit.MILLISECONDS);
                        } finally {
                            if (leader == thisThread)
                                leader = null;
                        }
                    }
                }
            }
        } finally {
            if (leader == null && doPeek(connect) != null)
                available.signal();
            lock.unlock();
        }
    }


    /**
     * 获取消息并从redis中删除
     * @param msgId
     * @param connect
     * @return
     * @throws IOException
     */
    private Message takeMessage(final String msgId, Jedis connect) throws IOException {
        String json = connect.hget(messageStoreKey, msgId);
        if (null != json) {
            Message ret = om.readValue(json, Message.class);
            connect.hdel(messageStoreKey, msgId);
            connect.zrem(queueShard, msgId);
            return ret;
        }
        return null;
    }


    private Message peek(Jedis connect) throws IOException {
        Tuple tuple = doPeek(connect);
        if (null != tuple && (tuple.getScore() - System.currentTimeMillis() <= 0)) {
            // 到期
            String msgId = tuple.getElement();
            String json = connect.hget(messageStoreKey, msgId);
            if (null != json) {
                return om.readValue(json, Message.class);
            }
        }

        return null;
    }


    private Tuple doPeek(Jedis connect) {
        return doPeek(0,connect);
    }

    /**
     *
     * @param timeout Message's timeout
     * @param connect
     * @return
     */
    private Tuple doPeek(int timeout, Jedis connect) {
        double score = (double)(System.currentTimeMillis() + timeout);
        Set<Tuple> st = connect.zrangeByScoreWithScores(queueShard, 0, score, 0, 1);
        if (st.isEmpty()) {
            return null;
        }
        return st.iterator().next();
    }

    private void push(Message message, Jedis connect) throws JsonProcessingException {
        double score = (double) (System.currentTimeMillis() + message.getTimeout());
        String json = om.writeValueAsString(message);
        connect.zadd(queueShard, score, message.getId());
        connect.hset(messageStoreKey, message.getId(), json);
    }


    private int push(final List<Message> messages, Jedis connect) throws JsonProcessingException {
        Pipeline pipe = connect.pipelined();

        long min = messages.get(0).getTimeout();
        int idx = 0;
        for( int i = 0; i < messages.size(); ++i ) {
            Message message = messages.get(i);
            double score = (double)(System.currentTimeMillis() + message.getTimeout());
            String json = om.writeValueAsString(message);
            pipe.zadd(queueShard, score, message.getId());
            pipe.hset(messageStoreKey, message.getId(), json);

            if( min > message.getTimeout() ) {
                min = message.getTimeout();
                idx = i;
            }
        }

        pipe.clear();
        return idx;
    }
}
