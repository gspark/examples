package com.shrill.singleton;

import java.io.Serializable;

public class SerSingleton implements Serializable {

    private static SerSingleton instance;

    private SerSingleton() {
        if (null != instance) {
            throw new RuntimeException();
        }
    }

    private String id = "999";

    public static synchronized SerSingleton getInstance() {
        if (instance == null) {
            instance = new SerSingleton();
        }
        return instance;
    }

    private Object readResolve() {
        return instance;
    }

}
