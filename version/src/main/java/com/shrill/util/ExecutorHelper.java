package com.shrill.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorHelper {

    private static final Logger log = LoggerFactory.getLogger(ExecutorHelper.class);

    private ThreadPoolExecutor ex;

    private static final int DELAY = 1000;

    private static final int LOOP = 3;

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
     */
    protected void executeAsync(Runnable command) {
        this.ex.execute(command);
    }

    public void executeAsync(DelayThread command) {
        this.ex.execute(command);
    }

    /**
     * 同步执行
     */
    public void execute(Runnable command) {
        command.run();
    }


    public static class DelayThread implements Runnable {

        private int seconds;

        private Callable<Integer> callable;

        public DelayThread(Callable<Integer> callable, int delay) {
            this.callable = callable;
            this.seconds = delay;
        }

        @Override
        public void run() {
            int count = 0;
            Integer ret;
            do {
                if (this.seconds != 0) {
                    try {
                        Thread.sleep(seconds);
                    } catch (InterruptedException e) {
                        log.info(e.getMessage(), e);
                    }
                }
                try {
                    ret = callable.call();
                    if (ret.equals(0)) {
                        return;
                    } else {
                        this.seconds = DELAY;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count++;
            } while (count < LOOP);
        }
    }
}
