package com.shrill.singleton;

public class Singleton implements Cloneable {

    private static Singleton instance;

    private Singleton() {
    }

    private String id = "999";

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
