package com.shrill;

import java.util.Random;

public class RandomTest {

    private Random random = new Random();

    public static void main(String[] args) {
        // 案例2
        // 对于种子相同的Random对象，生成的随机数序列是一样的。
        Random ran1 = new Random(8);
        System.out.println("使用种子为8的Random对象生成[0,10)内随机整数序列: ");
        for (int i = 0; i < 10; i++) {
            System.out.print(ran1.nextInt(10) + " ");
        }

        System.out.println();
        Random ran2 = new Random(8);
        System.out.println("使用另一个种子为8的Random对象生成[0,10)内随机整数序列: ");
        for (int i = 0; i < 10; i++) {
            System.out.print(ran2.nextInt(10) + " ");
        }
        System.out.println();
        System.out.println("使用同一个种子为8的Random对象生成[0,10)内随机整数序列: ");
        for (int i = 0; i < 10; i++) {
            System.out.print(ran2.nextInt(10) + " ");
        }

        /**
         * 输出结果为：
         *
         * 使用种子为8的Random对象生成[0,10)内随机整数序列:
         * 4 6 0 1 2 8 1 1 3 0
         * 使用另一个种子为8的Random对象生成[0,10)内随机整数序列:
         * 4 6 0 1 2 8 1 1 3 0
         *
         */

        // 案例3
        // 在没带参数构造函数生成的Random对象的种子缺省是当前系统时间的毫秒数。
        Random r3 = new Random();
        System.out.println();
        System.out.println("使用种子缺省是当前系统时间的毫秒数的Random对象生成[0,10)内随机整数序列");
        for (int i = 0; i < 10; i++) {
            System.out.print(r3.nextInt(10) + " ");
        }

        RandomTest randomTest = new RandomTest();
        System.out.println();
        System.out.println("使用种子缺省是当前系统时间的毫秒数的 RandomTest 成员变量Random对象生成[0,10)内随机整数序列, 第一次循环");
        for (int i = 0; i < 10; i++) {
            System.out.print(randomTest.nextInt(10) + " ");
        }

        System.out.println();
        System.out.println("使用种子缺省是当前系统时间的毫秒数的 RandomTest 成员变量Random对象生成[0,10)内随机整数序列, 第二次循环");
        for (int i = 0; i < 10; i++) {
            System.out.print(randomTest.nextInt(10) + " ");
        }

        System.out.println();
        System.out.println("使用种子缺省是当前系统时间的毫秒数的 RandomTest 成员变量Random对象生成[0,10)内随机整数序列, 第三次循环");
        for (int i = 0; i < 10; i++) {
            System.out.print(randomTest.nextInt(10) + " ");
        }
    }

    public int nextInt(int bound) {
        return this.random.nextInt(bound);
    }
}
