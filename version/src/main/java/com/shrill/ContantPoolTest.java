package com.shrill;

import java.util.ArrayList;
import java.util.List;

public class ContantPoolTest {

    public static void main(String[] args) {
        testIntern();
        return;
    }

    private static void test2() {
        List l = new ArrayList();
        int i = 0;
        while(true){
            l.add(String.valueOf(i++).intern());
        }
    }


    public static void test1() {
        String s = "abc";
        String s1 = "def";
        String s2 = s + s1 + "1123";

        System.out.println(s2);
    }

    public static void test3() {
        String s2 = "123" + "456";
        System.out.println(s2);
    }

    public static void testStringAdd() {
        String str = "";
        for (int i = 0; i < 1000; i++) {
            str = str + i;
        }
        System.out.println(str);
    }

    public static void testAddInt() {
        int i = 0;
        for (int j = 0; j < 100; j++) {
            i = ++i;
        }
        System.out.println(i);
    }

    public static void testIntAdd() {
        int i = 0;
        for (int j = 0; j < 1000; j++) {
            i = i++;
        }
        System.out.println(i);
    }

    public static void testPoolException() {
        //保持引用，防止自动垃圾回收
        List<String> l = new ArrayList<>();
        int i = 0;
        while(true){
            //通过intern方法向常量池中手动添加常量
            l.add(String.valueOf(++i).intern());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void testIntern() {
        String s1 = new StringBuilder("Intern").append("StringTest").toString();
        System.out.println(s1.intern() == s1);

        String s2 = new StringBuilder("CP").append("936").toString();
        System.out.println(s2.intern() == s2);
    }
}
