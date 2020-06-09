package com.shrill.example;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class StreamExp {

    public enum Type {MEAT, FISH, OTHER}

    public static class Dish {

        private final String name;
        private final boolean vegetarian;
        private final int calories;
        private final Type type;

        public Dish(String name, boolean vegetarian, int calories, Type type) {
            this.name = name;
            this.vegetarian = vegetarian;
            this.calories = calories;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public boolean isVegetarian() {
            return vegetarian;
        }

        public int getCalories() {
            return calories;
        }

        public Type getType() {
            return type;
        }

        @Override
        public String toString() {
            return name;
        }

        public enum Type {MEAT, FISH, OTHER}
    }


    private static final int GOOD = 90;

    List<Student> students = Arrays.asList(
        new Student("tom", 80, Student.Type.MALE),
        new Student("jerry", 70, Student.Type.FEMALE),
        new Student("小李", 90, Student.Type.FEMALE),
        new Student("小张", 93, Student.Type.MALE),
        new Student("小明", 35, Student.Type.MALE),
        new Student("大熊", 82, Student.Type.MALE),
        new Student("小雨", 55, Student.Type.FEMALE),
        new Student("二狗", 98, Student.Type.MALE),
        new Student("三胖", 59, Student.Type.MALE),
        new Student("duck", 50, Student.Type.MALE),
        new Student("mikey", 61, Student.Type.MALE),
        new Student("sam", 70, Student.Type.FEMALE),
        new Student("蓝胖", 99, Student.Type.MALE));

    List<Dish> menu = Arrays.asList(
        new Dish("pork", false, 800, Dish.Type.MEAT),
        new Dish("beef", false, 700, Dish.Type.MEAT),
        new Dish("chicken", false, 400, Dish.Type.MEAT),
        new Dish("french fries", true, 530, Dish.Type.OTHER),
        new Dish("rice", true, 350, Dish.Type.OTHER),
        new Dish("season fruit", true, 120, Dish.Type.OTHER),
        new Dish("pizza", true, 550, Dish.Type.OTHER),
        new Dish("prawns", false, 300, Dish.Type.FISH),
        new Dish("salmon", false, 450, Dish.Type.FISH));

    public static void main(String[] args) {
        StreamExp streamExp = new StreamExp();

        // System.out.println("Java 7");
        // streamExp.studentNames();
        // System.out.println("Java 8");
        // streamExp.studentNamesJ8();

        // System.out.println("more");
        // streamExp.more();

        System.out.println("studentNamesPrint");
        streamExp.studentNamesPrint();
    }

    public void studentNames() {
        List<Student> goodStudents = new ArrayList<>();
        for (Student d : students) {
            if (d.getScore() >= GOOD) {
                goodStudents.add(d);
            }
        }
        goodStudents.sort(new Comparator<Student>() {
            public int compare(Student d1, Student d2) {
                return Integer.compare(d1.getScore(), d2.getScore());
            }
        });
        List<String> goodStudentNames = new ArrayList<>();
        for (Student d : goodStudents) {
            goodStudentNames.add(d.getName());
        }
        System.out.println(goodStudentNames);
    }

    public void studentNamesJ8() {
        List<String> goodStudentNames = students.stream()
            .filter(s -> s.getScore() >= GOOD)
            .sorted(comparing(Student::getScore))
            .map(Student::getName)
            .collect(toList());

        System.out.println(goodStudentNames);
    }

    public void studentNamesLimit() {
        List<String> goodStudentNames = students.stream()
            .filter(s -> s.getScore() >= GOOD)
            .sorted(comparing(Student::getScore))
            .map(Student::getName)
            .limit(3)
            .collect(toList());

        System.out.println(goodStudentNames);
    }

    public void more() {
        List<String> title = Arrays.asList("Java8", "Stream", "Example");
        Stream<String> s = title.stream();
        s.forEach(System.out::println);
        s.forEach(System.out::println);
    }

    public void studentNamesPrint1() {
        List<String> goodStudentNames = students.stream()
            .filter(s -> {
                System.out.println("filter-> " + s.getName());
                return s.getScore() >= GOOD;
            })
            .sorted(comparing(s -> {
                System.out.println("sorted-> " + s.getName());
                return s.getScore();
            }))
            .map(s -> {
                System.out.println("map-> " + s.getName());
                return s.getName();
            })
            .limit(3)
            .collect(toList());

        System.out.println(goodStudentNames);
    }

    public void studentNamesPrint() {
        List<String> goodStudentNames = students.stream()
            .filter(s -> {
                System.out.println("filter-> " + s.getName());
                return s.getScore() >= GOOD;
            })
            .map(s -> {
                System.out.println("map-> " + s.getName());
                return s.getName();
            })
            .limit(3)
            .collect(toList());

        System.out.println(goodStudentNames);
    }

    public void menuPrint() {
        List<String> names =
            menu.stream()
                .filter(d -> {
                    System.out.println("filtering " + d.getName());
                    return d.getCalories() > 300;
                })
                .map(d -> {
                    System.out.println("mapping " + d.getName());
                    return d.getName();
                })
                .limit(3)
                .collect(toList());
        System.out.println(names);
    }
}