package com.shrill.util;

import java.io.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesHelper {

    private static Properties prop;
    static {
        loadProperty("db.properties");
    }

    public static void loadProperty(String filename) {
        prop = new Properties();
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(filename);
            if (inputStream == null) {
                System.out.println("Sorry, unable to find " + filename);
                return;
            }
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String property) {
        return prop.getProperty(property);
    }

    public static void setProperty(String key, String value) {
        prop.setProperty(key, value);
    }

    public static void put(Object key, Object value) {
        prop.put(key, value);
    }

    public static void store(OutputStream out, String comments) {
        try {
            prop.store(out, comments);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void storeXML(OutputStream out, String comments) {
        try {
            prop.storeToXML(out, comments);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<Map.Entry<Object, Object>> entrySet() {
        return prop.entrySet();
    }

    public static Set<Object> keySet() {
        return prop.keySet();
    }

    public static Enumeration<?> propertyNames(){
        return prop.propertyNames();
    }

    public static void list(PrintStream out){
        prop.list(out);
    }

    public static void printEntry() {
        Set<Map.Entry<Object, Object>> props = PropertiesHelper.entrySet();
        for (Map.Entry<Object, Object> entrys : props) {
            System.out.println(entrys.getKey() + ":" + entrys.getValue());
        }
    }

    public static void printKey() {
        Set<Object> keys = PropertiesHelper.keySet();
        for (Object obj : keys) {
            System.out.println(obj + ":" + PropertiesHelper.getProperty(obj.toString()));
        }
    }

    public static void printEnumeration(){
        Enumeration<?> e = PropertiesHelper.propertyNames();
        while(e.hasMoreElements()){
            String key = (String) e.nextElement();
            String value = PropertiesHelper.getProperty(key);
            System.out.println(key+":"+value);
        }
    }

    public static String getJarPath() {
        java.net.URL url = PropertiesHelper.class.getProtectionDomain().getCodeSource()
            .getLocation();
        String filePath = null;
        try {
            filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf(File.pathSeparator) + 1);
        }
        java.io.File file = new java.io.File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }
}
