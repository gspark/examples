package com.shrill;

import com.shrill.redisQueue.DelayTask;
import com.shrill.redisQueue.TaskClient;
import com.shrill.redisQueue.TaskServer;
import com.shrill.util.ExecutorHelper;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import sun.nio.ch.IOUtil;

public class TaskTest {

    private static JedisPool pool;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);
        config.setTestOnCreate(true);
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(60_000);
        pool = new JedisPool(config, "192.168.31.242", 6379, 2000, "Qwe123");
    }

    @Before
    public void Client() {
        TaskClient tc = new TaskClient(pool);

        try {
            tc.addTask("1", (Runnable & Serializable) () -> {
                    System.out.println("1");
                },
                30);

            tc.addTask("2", (Runnable & Serializable) () -> {
                    System.out.println("2");
                },
                30);

            tc.addTask("3", (Runnable & Serializable) () -> {
                    System.out.println("3");
                },
                30);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testServer() {
        TaskServer ts = new TaskServer(pool);
        ts.start();
        //ts.stop();
    }

    @Test
    public void testFormat() {
        long orderId = 1111111111111112345L;

        long orderId1 = 345L;
        String s = String.format("尾号%d的订单，用户提醒您尽快发货哦，赶紧去处理吧！", orderId % 10000);

        System.out.println(s);

        String s1 = String.format("尾号%d的订单，用户提醒您尽快发货哦，赶紧去处理吧！", orderId1 % 10000);

        System.out.println(s1);
    }

    @Test
    public void testRefund() {
        int getRefundTotalPrice = 1;
        Integer getSkuPriceOnline = new Integer(10);
        short getRefundGoodsQty = 1;
        int getCouponFee = 9;
        Short getGoodsQty = 1;
        if (getRefundTotalPrice <= 0 ||
            (getSkuPriceOnline.intValue() * getRefundGoodsQty - (int) (
                getCouponFee * getRefundGoodsQty
                    / getGoodsQty.intValue())) != getRefundTotalPrice) {
//      log.warn(
//          "refundTotalPrice: {} , skuPriceOnline: {} , couponFee: {} , goodsQty: {} ",
//          getRefundTotalPrice, getSkuPriceOnline,
//          getCouponFee, getGoodsQty);
            System.out.println("error");
        }
    }

    public enum AuditStatusEnum {
        apply,
        success,
        fail,;

        public static AuditStatusEnum getAuditStatusEnum(String code) {
            switch (code) {
                case "success":
                    return success;
                case "fail":
                    return fail;
                case "apply":
                    return apply;
            }
            return null;
        }
    }

    public class TestDate {

        private Date test;

        public Date getTest() {
            return test;
        }

        public void setTest(Date test) {
            this.test = test;
        }
    }

    @Test
    public void testSwitch() {
        System.out.println(AuditStatusEnum.getAuditStatusEnum(null));
    }

    @Test
    public void testDate() {
        System.out.println(new TestDate().getTest());

        Long l = 0L;
        System.out.println(l);
    }


    @Test
    public void testSplit() {
        String aaa = "asdfasdfa,asdfasfa";
        String[] d = aaa.split(",");
        System.out.println(d.length);

        aaa = "asdfasdfas";
        d = aaa.split(",");
        System.out.println(d[0]);

        aaa = "asdfasdfas,";
        d = aaa.split(",");
        System.out.println(d[0]);

        aaa = ",,,asdfasdfas,";
        d = aaa.split(",");
        System.out.println(d[0]);

        String[] bbb = ",a,,b,".split(",");
        System.out.println(bbb);

    }

    @Test
    public void testArrary() {
        String[] aaa;
        aaa = new String[0];

        System.out.println(aaa.length);
    }

    @Test
    public void testExecutorHelper() {
        System.out.println(Thread.currentThread().getName());
        final int[] aaa = {0};
        new ExecutorHelper().executeAsync(new ExecutorHelper.DelayThread(() -> {

            System.out.println("aaa " + Thread.currentThread().getName());

            if (aaa[0] == 0) {
                aaa[0] = 1;
                return 1;
            }
            return 0;
        }, 0));

        System.out.println("Thread.currentThread().getName()" + Thread.currentThread().getName());

//    try {
//      Thread.sleep(30000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
    }


    private String formatComment(String comment) {
        if (comment.length() <= 10) {
            return comment;
        }
        StringBuilder sb = new StringBuilder(comment);
        sb.replace(5, comment.length() - 5, "...");
        sb.append("\r\n\r\n").append(comment.substring(0, 5)).append("...")
            .append(comment.substring(comment.length() - 5));
        return sb.toString();
    }

    @Test
    public void testLonglong() {
        Long aaa = null;
        long bbb = aaa;
        System.out.println(bbb);
    }

    @Test
    public void formatCommentTest() {
        System.out.println(formatComment("qwerty"));
        System.out.println(formatComment("1234567890"));
        System.out.println(formatComment("没,有NB的加密，竟然卖这个价，学锤子玩情怀吧."));

        List<Integer>[] genericArray = (List<Integer>[]) new ArrayList[10];
//        Map<String, String>[] wp = new HashMap<String, String>[5];

    }


    @Test
    public void hashCodeTest() {
//        String value = "j_merchant_sms_reg_forgetpassword_18980084454";
        String value = "j_merchant_sms_reg_forgetpassword_13080084455";
        int ret = value.hashCode();
        System.out.println(ret);
    }
}
