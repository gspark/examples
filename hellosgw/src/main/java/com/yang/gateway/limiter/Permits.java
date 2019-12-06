package com.yang.gateway.limiter;


/**
 *
 * @param maxPermits 最大token数量
 * @param currentPermits 当前token数量
 * @param rate 每秒添加token数量
 * @param lastMilliSecond 上一次更新时间
 * @author jevon
 */
public class Permits {

    long maxPermits = 1;

    long currentPermits = 2;

    long rate = 3;

    long lastMilliSecond = 4;

    public Permits() {
    }

    public Permits(long maxPermits, long currentPermits, long rate, long lastMilliSecond) {
        this.maxPermits = maxPermits;
        this.currentPermits = currentPermits;
        this.rate = rate;
        this.lastMilliSecond = lastMilliSecond;
    }

    public long getMaxPermits() {
        return maxPermits;
    }

    public void setMaxPermits(long maxPermits) {
        this.maxPermits = maxPermits;
    }

    public long getCurrentPermits() {
        return currentPermits;
    }

    public void setCurrentPermits(long currentPermits) {
        this.currentPermits = currentPermits;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public long getLastMilliSecond() {
        return lastMilliSecond;
    }

    public void setLastMilliSecond(long lastMilliSecond) {
        this.lastMilliSecond = lastMilliSecond;
    }
}
