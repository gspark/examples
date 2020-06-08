package com.shrill.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class CodeReview3 {

    public static void main(String[] args) {
        createExecName();
        createExecName1();
        createExecName2();
    }

    public int getTabCounts(String filePath) {
        BufferedReader br = null;
        int tableCounts = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("<table")) {
                    tableCounts = tableCounts + 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return tableCounts;
    }


    public int getTabCounts1(String filePath) {
        int tableCounts = 0;

        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("<table")) {
                    tableCounts++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tableCounts;
    }

    private static String createExecName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();

        String ret = sdf.format(calendar.getTime()) + ".xlsx";
        System.out.println(ret);
        return ret;
    }

    private static String createExecName1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String ret = sdf.format(new Date()) + ".xlsx";
        System.out.println(ret);
        return ret;
    }

    private static String createExecName2() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String ret = LocalDateTime.now().format(formatter) + ".xlsx";
        System.out.println(ret);
        return ret;
    }
}
