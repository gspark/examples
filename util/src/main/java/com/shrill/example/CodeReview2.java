package com.shrill.example;

public class CodeReview2 {

    public static void main(String[] args) {
        int i = 123;
        String s = "" + i;
        System.out.println(s);

        String s1 = String.valueOf(i);
        System.out.println(s1);
    }
}
