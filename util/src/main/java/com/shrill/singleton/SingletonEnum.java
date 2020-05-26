package com.shrill.singleton;

import java.io.Serializable;

public enum SingletonEnum implements Serializable, Cloneable {
    INSTANCE;

    private String id = "999";

    SingletonEnum() {
        System.out.println("SingletonEnum create");
    }

    public int getHashCode() {
        return this.hashCode();
    }


}
