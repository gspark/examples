package com.yang.gateway.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;

/**
 * Copyright (C), 2018 - 2020
 * Author:   spark
 * Date:     2019-12-30
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public abstract class AbstractInfoCacheService implements DisposableBean {

    private static final Logger log = getLogger(AbstractInfoCacheService.class);

    private BlockingQueue<String> queue;

    private final ThreadPoolExecutor ex;

    private String nameFormat = null;

    private volatile boolean running;

    public AbstractInfoCacheService() {

        nameFormat = "LogCacheService-%d";
        final AtomicLong count = new AtomicLong(0L);
        // 队列大小
        final int size = 30;
        queue = new ArrayBlockingQueue<>(size);
        this.ex = new ThreadPoolExecutor(2, Runtime.getRuntime().availableProcessors(), 0, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, String.format(nameFormat, count.getAndIncrement()));
                    t.setDaemon(true);
                    return t;
                }
            });
        running = true;
        this.ex.execute(() -> {
            while (running) {
                String info = null;
                try {
                    info = this.queue.poll(60 * 3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                    running = false;
                }
                if (null != info && info.length() > 0) {
                    doSend(info);
                    batchSend();
                }
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        running = false;
        this.ex.shutdown();
        batchSend();
    }

    /**
     * 发送信息
     *
     * @param info
     */
    protected abstract void doSend(String info);

    protected void cache(String data) {
        this.ex.execute(() -> {
            try {
                boolean offer = this.queue.offer(data, 1, TimeUnit.SECONDS);
                if (!offer) {
                    // 入队超时，队列满了
                    batchSend();
                    this.queue.put(data);
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    /**
     * 批量发送
     */
    private void batchSend() {
        String info = null;
        do {
            try {
                info = this.queue.poll(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            if (null != info && info.length() > 0) {
                doSend(info);
            }
        } while (info != null);
    }
}
