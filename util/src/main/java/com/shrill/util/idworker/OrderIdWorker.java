package com.shrill.util.idworker;


/**
 * Created by zg on 2017-9-7.
 */
public class OrderIdWorker extends IdWorker {

    private static class SingletonHolder{
        private static OrderIdWorker instance = new OrderIdWorker();
    }

    public static OrderIdWorker getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * @return 订单id
     */
    public synchronized long nextId(long workerId, long userId) {
        setWorkerId(workerId);
        setUserId(userId);
        return this.nextId();
    }
}
