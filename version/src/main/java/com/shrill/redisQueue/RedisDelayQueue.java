package com.shrill.redisQueue;

import java.io.IOException;
import java.util.List;
import redis.clients.jedis.Jedis;

public interface RedisDelayQueue {

    /**
     * 根据消息id删除队列中的消息
     * 
     * @param messageId
     * @param connect
     * @return
     */
    boolean remove(String messageId, Jedis connect);


    /**
     * 根据消息id删除队列中的消息
     *
     * @param messageIds
     * @param connect
     * @return
     */
    void remove(final List<String> messageIds, Jedis connect);


    /**
     * 线程安全，阻塞的消息入队
     * 
     * @param message
     * @param connect
     * @return
     * @throws IOException
     */
    boolean offer(Message message, Jedis connect) throws IOException;


    /**
     * 线程安全，阻塞的消息入队
     *
     * @param messages
     * @param connect
     * @return
     * @throws IOException
     */
    boolean offer(final List<Message> messages, Jedis connect) throws IOException;

    /**
     * 阻塞消息出队，直到有到期的消息
     *
     * @param connect
     * @return Message 如果null，数据有错误
     * @throws InterruptedException
     * @throws IOException
     */
    Message take(Jedis connect) throws InterruptedException, IOException;
}
