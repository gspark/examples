package com.shrill.example;

public class MemModle1 {

    int i;                            //普通变量
    final int j;                      //final变量
    static MemModle1 obj;

    public MemModle1() {
        i = 1;                        //写普通域
        j = 2;                        //写final域
    }

    public static void writer() {    //写线程A执行
        obj = new MemModle1();
    }

    public static void reader() {       //读线程B执行
        MemModle1 object = obj;       //读对象引用
        int a = object.i;                //读普通域
        int b = object.j;                //读final域
    }
}
