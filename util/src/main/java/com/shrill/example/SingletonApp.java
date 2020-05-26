package com.shrill.example;

import com.shrill.singleton.SerSingleton;
import com.shrill.singleton.SingletonEnum;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SingletonApp {

    public static void main(String[] args) {
        // singleton();
        // serSingleton();

        // singletonEnum();
        singletonEnum1();
    }

    public static void singleton() {
        com.shrill.singleton.Singleton s = com.shrill.singleton.Singleton.getInstance();
        com.shrill.singleton.Singleton s1 = null;
        try {
            s1 = (com.shrill.singleton.Singleton) s.clone();
            System.out.println("s  hashCode:" + s.hashCode());
            System.out.println("s1 hashCode:" + s1.hashCode());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public static void serSingleton() {
        SerSingleton s1 = null;
        SerSingleton s2 = null;
        SerSingleton s = SerSingleton.getInstance();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("z:\\a.bin"))) {
            oos.writeObject(s);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("z:\\a.bin"))) {
            s1 = (SerSingleton) ois.readObject();
            if (null != s1) {
                System.out.println("s  hashCode:" + s.hashCode());
                System.out.println("s1 hashCode:" + s1.hashCode());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("z:\\a.bin"))) {
            s2 = (SerSingleton) ois.readObject();
            if (null != s2) {
                System.out.println("s2 hashCode:" + s2.hashCode());
                System.out.println("s equals s2 " + s.equals(s2));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void singletonEnum() {
        SingletonEnum s = SingletonEnum.INSTANCE;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("z:\\b.bin"))) {
            oos.writeObject(s);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SingletonEnum s1 = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("z:\\b.bin"))) {
            s1 = (SingletonEnum) ois.readObject();
            if (null != s1) {
                System.out.println("s  hashCode:" + s.hashCode());
                System.out.println("s1 hashCode:" + s1.hashCode());

                System.out.println("s equals s1 " + s.equals(s1));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void singletonEnum1() {
        SingletonEnum s = SingletonEnum.INSTANCE;
        System.out.println("s  hashCode:" + s.getHashCode());

        Class<?> clz = SingletonEnum.class;
        try {
            Constructor<?> c = clz.getConstructor();
            Object rcvr = c.newInstance();

            Method method = rcvr.getClass().getMethod("getHashCode");
            method.invoke(rcvr);
        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException
            | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
