package com.shrill.util;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;

public class Reflect {

    private static final Logger log = getLogger(Reflect.class);

    /**
     * 获得类的成员变量值，包括私有成员
     *
     * @param instance 被调用的类
     * @param variableName 成员变量名
     */
    public static Object getField(Object instance, String variableName) {
        Class targetClass = instance.getClass();
        return getObject(instance, variableName, targetClass);
    }

    /**
     * 获得父类的成员变量值，包括私有成员
     *
     * @param instance 被调用的类
     * @param variableName 成员变量名
     */
    public static Object getFieldFromSuperclass(Object instance, String variableName) {
        Class targetClass = instance.getClass().getSuperclass();
        return getObject(instance, variableName, targetClass);
    }

    private static Object getObject(Object instance, String variableName, Class targetClass) {
        Field field;
        try {
            field = targetClass.getDeclaredField(variableName);
            //访问私有必须调用
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            log.info(e.getMessage(),e);
            return null;
        }
    }

    public static boolean setField(Object instance, String variableName, Object value) {
        Class targetClass = instance.getClass();
        Field field;
        try {
            field = targetClass.getDeclaredField(variableName);

            //访问私有必须调用
            field.setAccessible(true);
            field.set(instance, value);

            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            log.info(e.getMessage(),e);
        }
        return false;
    }


    /**
     * 执行方法
     *
     * @param instance 调用此方法的对象
     * @param MethodName 方法名
     * @param paras 调用的这个方法的参数参数列表
     */
    public static void invokeMethod(Object instance, String MethodName, Object[] paras) {
        Class c[] = getParameterTypes(paras);
        try {
            Method method = instance.getClass().getDeclaredMethod(MethodName, c);
            //调用o对象的方法
            method.invoke(instance, paras);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            log.info(e.getMessage(), e);
        }
    }

    /**
     * 执行父级方法
     *
     * @param instance 调用此方法的对象
     * @param MethodName 方法名
     * @param paras 调用的这个方法的参数参数列表
     */
    public static Object invokeMethodFromSuperclass(Object instance, String MethodName,
        Object[] paras) {
        Class c[] = getParameterTypes(paras);
        Object ret = null;
        try {
            Method method = instance.getClass().getSuperclass().getDeclaredMethod(MethodName, c);
            boolean setAccessed = false;
            if (!method.isAccessible()) {
                setAccessed = true;
                method.setAccessible(true);
            }
            //调用o对象的方法
            ret = method.invoke(instance, paras);
            if (setAccessed) {
                method.setAccessible(false);
            }

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            log.info(e.getMessage(), e);
        }
        return ret;
    }

    private static Class[] getParameterTypes(Object[] paras) {
        Class c[] = null;
        if (paras != null) {
            //存在
            int len = paras.length;
            c = new Class[len];
            for (int i = 0; i < len; ++i) {
                c[i] = paras[i].getClass();
            }
        }
        return c;
    }
}
