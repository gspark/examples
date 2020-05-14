package com.shrill;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class RandomTest {

    private Random random = new Random();

    public static void main(String[] args) {
        // mathRandom();
        // sameSeed();
        // defaultSeed();


        // invokeHashCode();
        // invokeHashCode1();
        // invokeSameSeed();
        invokeSameSeed1();
    }

    private static void random() {
        // 案例2
        // 对于种子相同的Random对象，生成的随机数序列是一样的。
        sameSeed();

        // 案例3
        // 在没带参数构造函数生成的Random对象的种子缺省是当前系统时间。
        defaultSeed();

        RandomTest randomTest = new RandomTest();
        System.out.println();
        System.out.println("使用种子缺省是当前系统时间的 RandomTest 成员变量Random对象生成[0,10)内随机整数序列, 第一次循环");
        for (int i = 0; i < 10; i++) {
            System.out.print(randomTest.nextInt(10) + " ");
        }

        System.out.println();
        System.out.println("使用种子缺省是当前系统时间的 RandomTest 成员变量Random对象生成[0,10)内随机整数序列, 第二次循环");
        for (int i = 0; i < 10; i++) {
            System.out.print(randomTest.nextInt(10) + " ");
        }

        System.out.println();
        System.out.println("使用种子缺省是当前系统时间的 RandomTest 成员变量Random对象生成[0,10)内随机整数序列, 第三次循环");
        for (int i = 0; i < 10; i++) {
            System.out.print(randomTest.nextInt(10) + " ");
        }
    }

    private static void defaultSeed() {
        Random r3 = new Random();
        System.out.println();
        System.out.println("使用种子缺省是当前系统时间的Random对象生成[0,10)内随机整数序列");
        for (int i = 0; i < 10; i++) {
            System.out.print(r3.nextInt(10) + " ");
        }

        // 案例4
        // 在没带参数构造函数生成的Random对象的种子缺省是当前系统时间。
        Random r4 = new Random();
        System.out.println();
        System.out.println("使用种子缺省是当前系统时间的Random对象生成[0,10)内随机整数序列");
        for (int i = 0; i < 10; i++) {
            System.out.print(r4.nextInt(10) + " ");
        }
    }

    private static void sameSeed() {
        Random ran1 = new Random(8);
        System.out.println("使用种子为8的Random对象生成[0,10)内随机整数序列:");
        for (int i = 0; i < 10; i++) {
            System.out.print(ran1.nextInt(10) + " ");
        }

        System.out.println();
        Random ran2 = new Random(8);
        System.out.println("使用另一个种子为8的Random对象生成[0,10)内随机整数序列:");
        for (int i = 0; i < 10; i++) {
            System.out.print(ran2.nextInt(10) + " ");
        }
        System.out.println();
        System.out.println("使用同一个种子为8的Random对象生成[0,10)内随机整数序列:");
        for (int i = 0; i < 10; i++) {
            System.out.print(ran2.nextInt(10) + " ");
        }
    }

    public int nextInt(int bound) {
        return this.random.nextInt(bound);
    }

    public static void mathRandom() {
        System.out.println("Math.random()=" + Math.random());
        // 结果是个double类型的值，区间为[0.0,1.0）
        int num = (int) (Math.random() * 3);
        // 注意不要写成(int)Math.random()*3，这个结果为0，因为先执行了强制转换
        System.out.println("num=" + num);
    }

    public static void invokeHashCode() {
        Object rcvr = "a";
        try {
            Class<?>[] argTypes = new Class[]{};
            Object[] args = null;

            Method method = rcvr.getClass().getMethod("hashCode", argTypes);
            Object ret = method.invoke(rcvr, args);
            System.out.println(ret);

            System.out.println("a".hashCode());

        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException
            | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void invokeHashCode1() {
        Class<?> clz = String.class;
        try {
            Class<?>[] argTypes = new Class[]{};
            Object[] args = null;

            Constructor<?> c = clz.getConstructor(String.class);
            Object rcvr = c.newInstance("b");

            Method method = rcvr.getClass().getMethod("hashCode", argTypes);
            Object ret = method.invoke(rcvr, args);
            System.out.println(ret);

        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException
            | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void invokeSameSeed() {
        Class<?> clz = RandomTest.class;
        try {
            Constructor<?> c = clz.getConstructor();
            Object rcvr = c.newInstance();

            Method method = rcvr.getClass().getDeclaredMethod("sameSeed");
            method.setAccessible(true);
            method.invoke(rcvr);
        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException
            | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void invokeSameSeed1() {
        try {
            Method method = RandomTest.class.getDeclaredMethod("sameSeed");
            // method.setAccessible(true);
            method.invoke(null);
        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException
            | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
