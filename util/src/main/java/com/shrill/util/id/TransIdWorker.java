package com.shrill.util.id;

/**
 * Created by zg on 2017-5-16.
 */
public class TransIdWorker {

    private static TransIdWorker instance = new TransIdWorker();
    
    // 2017-01-01 00:00:00
    private final static long epoch = 1483200000000L; 

    private long workerId;
    
    private long orderId;

    private long sequence = 0L;

    // 8位工作机id 最大256台机器
    private final static long workerIdBits = 8L;

    // 8位递增序列 每毫秒256个序列
    private final static long sequenceBits = 8L;
    
    // 8位 orderId的最后8位,也是userId的后8位
    private final static long orderIdBits = 8L;

    private final static long workerIdShift = sequenceBits + orderIdBits;

    // timestamp 39位
    private final static long timestampLeftShift = workerIdBits + workerIdShift;

    private long lastTimestamp = -1L;

    public final static long maxWorkerId = -1L ^ (-1L << workerIdBits);

    public final static long sequenceMask = -1L ^ (-1L << sequenceBits);

    private TransIdWorker(final long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {

            throw new IllegalArgumentException(String.format(
                "worker Id can't be greater than %d or less than 0",
                maxWorkerId));
        }
        this.workerId = workerId;
    }
    
    private TransIdWorker(){
    }
    
    public static TransIdWorker getInstance(final long workerId){
        instance.setWorkerId(workerId);
        return instance;
    }

    /**
     * 订单号 64bit 整型数
     * 组成  符号      时间戳     workId    序列号    orderId的最后8位
     *      ----    -------   ------    -----        -----
     * 位数   1        39        8        8            8
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

        long ret = ((timestamp - epoch) << timestampLeftShift)
            | (this.workerId << workerIdShift)
            | (this.sequence << orderIdBits)
            | (this.orderId & 0xFF);
        return ret;
    }
    
    public synchronized long nextId(long orderId) {
        this.orderId = orderId;
        return this.nextId();
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

    public void setWorkerId(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {

            throw new IllegalArgumentException(String.format(
                "worker Id can't be greater than %d or less than 0",
                maxWorkerId));
        }
        this.workerId = workerId;
    }
    
    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
