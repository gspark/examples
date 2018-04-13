package com.shrill.id;

/**
 * Created by zg on 2017-5-16.
 */
public class OrderIdWorker {

    private final static long epoch = 1483200000000L; //2017-01-01 00:00:00;

    private final long workerId;

    private long sequence = 0L;

    // 8位工作机id 最大256台机器
    private final static long workerIdBits = 8L;

    // 6位递增序列 每毫秒64个序列
    private final static long sequenceBits = 6L;

    // 3位随机数
    private final static long randomIdBits = 3L;

    private final static long workerIdShift = sequenceBits + randomIdBits;

    // timestamp 39位
    private final static long timestampLeftShift = workerIdBits + workerIdShift;

    private long lastTimestamp = -1L;

    public final static long maxWorkerId = -1L ^ (-1L << workerIdBits);

    public final static long sequenceMask = -1L ^ (-1L << sequenceBits);

    public OrderIdWorker(final long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {

            throw new IllegalArgumentException(String.format(
                "worker Id can't be greater than %d or less than 0",
                maxWorkerId));
        }
        this.workerId = workerId;
    }

    /**
     * 订单号 64bit 整型数
     * 组成  符号    保留    时间戳    workid    序列号    随机
     *      ----    ----    ------   ------    -----     ----
     * 位数   1       7       39        8        6         3
     * @return 长度17的数字
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
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
            | (this.sequence << randomIdBits)
            | (this.lastTimestamp & 0b111);

        nextId = nextId & 0xFFFFFFFFFFFFFFFL;

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
