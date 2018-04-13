package com.shrill;

import com.shrill.id.IdWorker;
import com.shrill.id.OrderIdExWorker;
import com.shrill.id.OrderIdWorker;
import com.shrill.id.SidWorker;
import com.shrill.id.TransIdWorker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class IdTest {

  @Test
  public void testApp() {
    IdWorker worker2 = new IdWorker(2);

    System.out.println(worker2.nextId());
  }

  @Test
  public void mulitId() {
    IdWorker worker2 = new IdWorker(3);

    int count = 100000;

    ExecutorService pool = Executors.newFixedThreadPool(count);

    List<Future<Long>> futures = new ArrayList<>(count);

    Set<Long> ls = new HashSet<>(count);
    for (int i = 0; i < count; ++i) {
      futures.add(pool.submit(() -> worker2.nextId()));
    }
    for (Future<Long> future : futures) {
      try {
        Long result = future.get();
        System.out.println(future.toString() + " id: " + result);
        ls.add(result);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }

      //Compute the result
    }
    pool.shutdown();
    System.out.println(ls.size());
  }

  public void testBlankList() {
    List<Integer> l = null;
    for (int a : l) {
      System.out.print(a);
    }
  }

  @Test
  public void OrderId() {
    OrderIdWorker worker1 = new OrderIdWorker(99);
    long id = worker1.nextId();
    System.out.println("id:" + id);
    Assert.assertTrue(99 == (id & 0b11111111111111111) >> 9L);
    long time = id >> 17L;
    System.out.println("ti:" + time);
  }

  @Test
  public void MultiOrderId() {
    OrderIdWorker worker1 = new OrderIdWorker(99);

    int count = 30000;

    ExecutorService pool = Executors.newFixedThreadPool(count);

    List<Future<Long>> futures = new ArrayList<>(count);

    Set<Long> ls = new HashSet<>(count);
    for (int i = 0; i < count; ++i) {
      futures.add(pool.submit(() -> worker1.nextId()));
    }
    for (Future<Long> future : futures) {
      try {
        Long result = future.get();
        System.out.println(" id: " + result);
        ls.add(result);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }

      //Compute the result
    }
    pool.shutdown();
    System.out.println(ls.size());
  }

  @Test
  public void testSid() {
    System.out.println(SidWorker.nextSid());
  }

  @Test
  public void cutId() {
    long aaa = 18888;
    aaa = aaa % 1000;

    System.out.println(aaa);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      Date date = sdf.parse(" 2017-01-01 00:00:00");
      System.out.println(date.getTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    final long YEAR_TIME = 1000 * 60 * 60 * 24l * 365;//固定每年的时间
    final long YEAR_DEVIATE = 40;//固定偏移40年

    StringBuffer order = new StringBuffer();
    order.append(new Date().getTime() - YEAR_TIME * YEAR_DEVIATE);//40年前的时间
    System.out.println(order.toString());
  }

  @Test
  public void OrderExId() {
    OrderIdExWorker worker1 = new OrderIdExWorker(99, 255);
    long id = worker1.nextId();
    System.out.println("id:" + id);
    Assert.assertTrue(99 == (id & 0b1111111111111111111111) >> 14L);

    Assert.assertTrue(255 == (id & 0xFF));
    long time = id >> 22L;
    System.out.println("ti:" + time);

    long a19 = 99999999999999999L;
    System.out.println("a19:" + a19);
  }

  @Test
  public void testTransId() {
    TransIdWorker worker = TransIdWorker.getInstance(99);
    worker.setOrderId(255);

    long id = worker.nextId();
    System.out.println("id:" + id);
    Assert.assertTrue(99 == (id & 0xFFFFFF) >> 16L);

    Assert.assertTrue(255 == (id & 0xFF));
    long time = id >> 24L;
    System.out.println("ti:" + time);

    System.out.println(worker.nextId());
  }

  @Test
  public void testList() throws InterruptedException {
      LinkedBlockingDeque<Integer> lbd = new LinkedBlockingDeque<>();
      lbd.put(1);
      lbd.put(2);
      lbd.put(3);
      lbd.put(4);

      Iterator<Integer> itr = lbd.iterator();

      lbd.poll();
      lbd.poll();
      lbd.poll();

      System.out.println(itr.next());
      System.out.println(itr.next());
  }
}
