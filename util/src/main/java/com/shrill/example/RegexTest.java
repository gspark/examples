package com.shrill.example;

import java.util.regex.Pattern;

public class RegexTest {

    public static boolean isMatched(final String version) {
        String regex = "^[1-9][0-9]{0,2}\\.[0-9]{1,3}\\.[0-9]{1,3}$";
        return Pattern.matches(regex,version);
    }

    public static void main(String[] args) {
        String v1 = "100.0.0";
        System.out.println("version:"+ v1 + " isMatched: " + isMatched(v1));
        String v2 = "010.0.0";
        System.out.println("version:"+ v2 + " isMatched: " + isMatched(v2));
        String v3 = "10.0000.0";
        System.out.println("version:"+ v3 + " isMatched: " + isMatched(v3));
        String v4 = "10.0.0000";
        System.out.println("version:"+ v4 + " isMatched: " + isMatched(v4));
        String v5 = "10.0.000";
        System.out.println("version:"+ v5 + " isMatched: " + isMatched(v5));
    }
}
