package com.shrill.util.id;

public class IdWorker {

    // 2017-01-01 00:00:00
    private final static long epoch = 1483200000000L;

    private long workerId;

    private long userId;

    private long sequence = 0L;

    private long lastTimestamp = -1L;

    // 8位工作机id 最大256台机器
    private final static long workerIdBits = 8L;

    // 6位递增序列 每毫秒64个序列
    private final static long sequenceBits = 6L;

    // 8位 userId的最后8位
    private final static long userIdBits = 8L;

    private final static long workerIdShift = sequenceBits + userIdBits;

    // timestamp 39位
    private final static long timestampLeftShift = workerIdBits + workerIdShift;

    public final static long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 订单号 64bit 整型数
     * 组成   符号     保留     时间戳    workId    序列号     userId的最后8位
     *      ----    ----    ------   ------    -----        ----
     * 位数   1       2       39        8        6            8
     * @return 长度17的数字
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            // 出现这个异常，继续调用，直到获取到更大的时间戳
            throw new IllegalStateException("Clock moved backwards.");
        }

        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1L) & sequenceMask;
            if (this.sequence == 0L) {
                timestamp = this.tilNextMillis(timestamp);
            }
        } else {
            this.sequence = 0L;
        }

        this.lastTimestamp = timestamp;

        long nextId = ((timestamp - epoch) << timestampLeftShift)
            | (this.workerId << workerIdShift)
            | (this.sequence << userIdBits)
            | (this.userId & 0xFF);

        nextId = nextId & ((-1L ^ (-1L << 63)) >> 2);

        return nextId;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public long getWorkerId() {
        return workerId;
    }

    public synchronized void setWorkerId(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {

            throw new IllegalArgumentException(String.format(
                "worker Id can't be greater than %d or less than 0",
                maxWorkerId));
        }
        this.workerId = workerId;
    }

    public long getUserId() {
        return userId;
    }

    public synchronized void setUserId(long userId) {
        this.userId = userId;
    }
}
