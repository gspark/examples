package com.shrill.example;

import static java.util.Comparator.comparing;

import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.EventListener;
import java.util.List;

public class LambdaExp {

    public interface ActionListener extends EventListener {

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e);

        // public void test(ActionEvent e);

    }

    public static void main(String[] args) {
    }

    public void sort(List<Student> students) {
        students.sort(new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getScore() - s2.getScore();
            }
        });
        long c = students.stream().filter((s) -> s.getScore() > 80).count();
    }

    public void sort2(List<Student> students) {
        students.sort(comparing(Student::getScore));
        students.sort(comparing((s) -> {
            return s.getScore();
        }));
    }

    public void simpleLambda1() {
        Runnable runnable = () -> System.out.println("hello world");
        new Thread(runnable).start();
    }

    public void simpleLambda2() {
        new Thread(() -> {
            System.out.println("hello world");
        }).start();
    }

    public void simpleLambda3() {
        String ss = "world";
        new Thread(() -> {
            System.out.println("hello " + ss);
        }).start();
    }

    public void simpleLambda4() {
        String ss = "world";
        new Thread(() -> {
            System.out.println("hello " + ss);
        }).start();
        // ss = "小明";
    }

    public void simpleLambda5() {
        ActionListener oneArgument = event -> System.out.println("button clicked");
    }
}
