package com.shrill.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zg on 2017-5-16.
 */
public class IdWorker {

    private final static long epoch = 1288834974657L; //2010-11-04 09:42:54.657 可到2080年;
    private final long workerId;

    private long sequence = 0L;

    // 16位工作机id
    private final static long workerIdBits = 16L;

    public final static long maxWorkerId = -1L ^ (-1L << workerIdBits);

    // 6位递增序列
    private final static long sequenceBits = 6L;

    private final static long timestampLeftShift = sequenceBits + workerIdBits;

    public final static long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    public IdWorker(final long workerId) {
        if (workerId > this.maxWorkerId || workerId < 0) {

            throw new IllegalArgumentException(String.format(
                "worker Id can't be greater than %d or less than 0",
                this.maxWorkerId));
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards.");
        }

        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1L) & this.sequenceMask;
            if (this.sequence == 0L) {
                timestamp = this.tilNextMillis(timestamp);
            }
        } else {
            this.sequence = 0L;
        }

        this.lastTimestamp = timestamp;

        long nextId = ((timestamp - epoch) << timestampLeftShift)
            | (this.workerId << this.sequenceBits)
            | (this.sequence);

        return nextId;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
