package com.shrill.util;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

public class ExecutorHelper {

    private static final Logger log = getLogger(ExecutorHelper.class);

    private static final int DELAY = 1000;

    private static final int LOOP = 4;

    private final ThreadPoolExecutor ex;

    public ExecutorHelper() {
        this("ExecutorHelper-%d");
    }

    public ExecutorHelper(String nameFormat) {
        this.ex = new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100),
            new ThreadFactoryBuilder()
                .setNameFormat(nameFormat)
                .setDaemon(true)
                .build());
    }

    public ExecutorHelper(ThreadPoolExecutor ex) {
        this.ex = ex;
    }

    /**
     * 异步执行
     *
     * @param command
     */
    protected void executeAsync(Runnable command) {
        this.ex.execute(command);
    }

    /**
     * 异步执行 带重试
     *
     * @param command
     */
    public void executeAsync(DelayThread command) {
        this.ex.execute(command);
    }

    /**
     * 同步执行
     *
     * @param command
     */
    public void execute(Runnable command) {
        command.run();
    }


    public static class DelayThread implements Runnable {

        private int delay;

        private Callable<Integer> callable;

        public DelayThread(Callable<Integer> callable, int delay) {
            this.callable = callable;
            this.delay = delay;
        }

        @Override
        public void run() {
            int count = 0;
            Integer ret;
            do {
                if (this.delay != 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                try {
                    ret = callable.call();
                    if (ret.equals(0)) {
                        return;
                    } else {
                        this.delay += DELAY;
                    }
                } catch (Exception e) {
                    log.info(e.getMessage(), e);
                }
                count++;
            } while (count < LOOP);
        }
    }
}
