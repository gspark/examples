package com.shrill.example;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    // 13
    List<Student> students = Arrays.asList(
        new Student("tom", 80, Student.Type.MALE),
        new Student("jerry", 71, Student.Type.FEMALE),
        new Student("小李", 90, Student.Type.FEMALE),
        new Student("小明", 35, Student.Type.MALE),
        new Student("大熊", 82, Student.Type.MALE),
        new Student("小雨", 90, Student.Type.FEMALE),
        new Student("二狗", 98, Student.Type.MALE),
        new Student("三胖", 59, Student.Type.MALE),
        new Student("duck", 50, Student.Type.MALE),
        new Student("mikey", 61, Student.Type.MALE),
        new Student("sam", 70, Student.Type.FEMALE),
        new Student("小张", 93, Student.Type.MALE),
        new Student("蓝胖", 99, Student.Type.MALE));

    // 9
    List<Dish> menu = Arrays.asList(
        new Dish("season fruit", true, 120, Dish.Type.OTHER),
        new Dish("pork", false, 800, Dish.Type.MEAT),
        new Dish("beef", false, 700, Dish.Type.MEAT),
        new Dish("chicken", false, 400, Dish.Type.MEAT),
        new Dish("french fries", true, 530, Dish.Type.OTHER),
        new Dish("rice", true, 350, Dish.Type.OTHER),
        new Dish("pizza", true, 550, Dish.Type.OTHER),
        new Dish("prawns", false, 300, Dish.Type.FISH),
        new Dish("salmon", false, 450, Dish.Type.FISH));

    public static void main(String[] args) {
        StreamExp streamExp = new StreamExp();

        // System.out.println("Java 7:");
        // streamExp.studentNames();
        // System.out.println("Java 8:");
        // streamExp.studentNamesJ8();

        // System.out.println("limit:");
        // streamExp.studentNamesLimit();

        // System.out.println("more:");
        // streamExp.more();

        // System.out.println("studentNamesPrint:");
        // streamExp.studentNamesPrint();

        // System.out.println("studentNamesPrintSort:");
        // streamExp.studentNamesPrintSort();

        // System.out.println("menuPrint:");
        // streamExp.menuPrint();

        // System.out.println("distinct:");
        // streamExp.distinct();

        // System.out.println("skip:");
        // streamExp.skip();

        // System.out.println("stringLength:");
        // streamExp.stringLength();

        // System.out.println("flatMapOfStream:");
        // streamExp.flatMapOfStream();

        // System.out.println("numberPair:");
        // streamExp.numberPair();

        // System.out.println("numberPairEx:");
        // streamExp.numberPairEx();

        // System.out.println("reduceEx:");
        // streamExp.reduceEx();

        // System.out.println("createStream:");
        // streamExp.createStream();

        // streamExp.collectEx();

        // streamExp.groupGet();
        // streamExp.groupGet1();
        // streamExp.groupGet3();
        streamExp.groupGet5();
    }

    public void studentNames() {
        List<Student> goodStudents = new ArrayList<>();
        for (Student d : students) {
            if (d.getScore() >= GOOD) {
                goodStudents.add(d);
            }
        }
        goodStudents.sort(new Comparator<Student>() {
            @Override
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
            .sorted(comparing(Student::getScore).reversed())
            .map(Student::getName)
            .limit(3)
            .collect(toList());

        System.out.println(goodStudentNames);
    }

    public void more() {
        List<String> title = Arrays.asList("Java8", "Stream", "Example");
        Stream<String> s = title.stream();
        s.forEach(System.out::println);
        s = title.stream();
        s.forEach(System.out::println);
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

    public void studentNamesPrintSort() {
        List<String> goodStudentNames = students.stream()
            .filter(s -> {
                System.out.println("filter-> " + s.getName());
                return s.getScore() >= GOOD;
            })
            .sorted(Comparator.comparing((Student s) -> {
                System.out.println("sorted-> " + s.getName());
                return s.getScore();
            }).reversed())
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
                // .sorted(comparing(d -> {
                //     System.out.println("sorted-> " + d.getName());
                //     return d.getCalories();
                // }))
                .map(d -> {
                    System.out.println("mapping " + d.getName());
                    return d.getName();
                })
                .limit(3)
                .collect(toList());
        System.out.println(names);
    }

    public void distinct() {
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4, 6);
        numbers.stream()
            .filter(i -> i % 2 == 0)
            .distinct()
            .forEach(System.out::println);
    }

    public void skip() {
        List<String> goodStudentNames = students.stream()
            .filter(s -> {
                System.out.println("filter-> " + s.getName());
                return s.getScore() > GOOD;
            })
            .skip(1)
            .map(s -> {
                System.out.println("map-> " + s.getName());
                return s.getName();
            })
            .limit(3)
            .collect(toList());

        System.out.println(goodStudentNames);
    }

    public void stringLength() {
        List<String> words = Arrays.asList("Java 8", "Lambdas", "In", "Action");
        List<Integer> wordLengths = words.stream()
            .map(String::length)
            .collect(toList());
        System.out.println(wordLengths);
    }

    public void flatMapOfStream() {
        List<String> words = Arrays.asList("Hello", "World");

        List<String[]> characters = words.stream()
            .map(word -> word.split(""))
            .distinct()
            .collect(toList());
        System.out.println(characters);

        List<Stream<String>> characters1 =
            words.stream()
                .map(w -> w.split(""))
                // 每个数组变成了单独的流
                .map(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(characters1);

        List<String> uniqueCharacters =
            words.stream()
                .map(w -> w.split(""))
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(uniqueCharacters);
    }

    public void numberPair() {
        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> numbers2 = Arrays.asList(3, 4);

        List<int[]> pairs = numbers1.stream()
            .flatMap(
                i -> numbers2.stream().map(j -> new int[]{i, j})
            )
            .collect(toList());

        System.out.print("[");
        pairs.forEach(p -> {
            System.out.print(String.format("(%d,%d)", p[0], p[1]));
        });
        System.out.print("]");
    }

    public void numberPairEx() {
        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> numbers2 = Arrays.asList(3, 4);

        List<int[]> pairs = numbers1.stream()
            .flatMap(
                i -> numbers2.stream()
                    .filter(j -> (i + j) % 3 == 0)
                    .map(j -> new int[]{i, j})
            )
            .collect(toList());

        System.out.print("[");
        pairs.forEach(p -> {
            System.out.print(String.format("(%d,%d)", p[0], p[1]));
        });
        System.out.print("]");
    }

    public void reduceEx() {
        List<Integer> numbers = Arrays.asList(4, 5, 3, 9);
        int sum = numbers.stream().reduce(0, (a, b) -> a + b);
        System.out.println("sum: " + sum);

        Optional<Integer> min = numbers.stream().reduce((x, y) -> x > y ? y : x);
        Optional<Integer> max = numbers.stream().reduce(Integer::max);

        System.out.println("min: " + min.orElse(0) + " max: " + max.orElse(0));
    }

    public void createStream() {
        Stream<String> stream = Stream.of("Java 8 ", "Lambdas ", "In ", "Action");
        stream.map(String::toUpperCase).forEach(System.out::println);

        int[] numbers = {2, 3, 5, 7, 10, 14};
        int sum = Arrays.stream(numbers).sum();
        System.out.println("sum:" + sum);

        Stream.iterate(0, n -> n + 2)
            .limit(10)
            .forEach(System.out::println);

        Stream.generate(Math::random)
            .limit(5)
            .forEach(System.out::println);
    }

    public void collectEx() {
        Comparator<Dish> dishCaloriesComparator =
            Comparator.comparingInt(Dish::getCalories);

        Optional<Dish> mostCalorieDish =
            menu.stream()
                .collect(maxBy(dishCaloriesComparator));
        mostCalorieDish.ifPresent(dish -> System.out.println(dish.name));
    }

    /**
     * 分组取值
     */
    public void groupGet() {
        Map<Student.Type, Student> m = students.stream().collect(
            Collectors.groupingBy(
                Student::getType,
                Collectors.collectingAndThen(
                    Collectors.maxBy(
                        Comparator.comparingInt(Student::getScore)), Optional::get
                )
            ));

        for (Map.Entry<Student.Type, Student> s : m.entrySet()) {
            System.out.println(s.getKey() + " " + s.getValue().toString());
        }
    }

    /**
     * 分组取最大值
     */
    public void groupGet1() {
        Map<Student.Type, Student> m = students.stream().collect(
            Collectors.toMap(Student::getType, Function.identity(),
                BinaryOperator.maxBy(Comparator.comparingInt(Student::getScore))));

        for (Map.Entry<Student.Type, Student> s : m.entrySet()) {
            System.out.println(s.getKey() + " " + s.getValue().toString());
        }
    }

    /**
     * 分组
     */
    public void groupGet3() {
        Map<Student.Type, Student> m = students.stream().collect(
            Collectors.groupingBy(Student::getType,
                Collectors.collectingAndThen(Collectors.reducing((s1, s2) -> s1.getScore() > s2.getScore() ? s1 : s2),
                    Optional::get)));

        for (Map.Entry<Student.Type, Student> s : m.entrySet()) {
            System.out.println(s.getKey() + " " + s.getValue().toString());
        }
    }

    /**
     * 分组5
     */
    public void groupGet5() {
        Map<Student.Type, List<Student>> m = students.stream().collect(
            Collectors.groupingBy(Student::getType
            ));

        for (Map.Entry<Student.Type, List<Student>> s : m.entrySet()) {
            System.out.println(s.getKey() + " " + s.getValue().toString());
        }
    }

    /**
     * 分组取值
     */
    public void groupGet2() {
        Map<Student.Type, Student> m = new HashMap<>();

        for (Student s : students) {
            Student tmp = m.get(s.getType());
            if (null == tmp) {
                m.put(s.getType(), s);
            } else {
                if (s.getScore() > tmp.getScore()) {
                    m.put(s.getType(), s);
                }
            }
        }
        for (Map.Entry<Student.Type, Student> s : m.entrySet()) {
            System.out.println(s.getKey() + " " + s.getValue().toString());
        }
    }

    public void groupGet4() {
        List<Student> ret = new ArrayList<>();
        for (Student s : students) {
            if (ret.isEmpty()) {
                ret.add(0, s);
            } else {
                boolean add = false;
                boolean sameType = false;
                for (int j = ret.size() - 1; j >= 0; j--) {
                    Student ss = ret.get(j);
                    if (ss.getType() == s.getType()) {
                        if (s.getScore() > ss.getScore()) {
                            add = true;
                            ret.remove(j);
                        } else if (s.getScore() == ss.getScore()) {
                            add = true;
                        }
                        sameType = true;
                    }
                }
                if (add || !sameType) {
                    ret.add(s);
                }
            }
        }
        for (Student s : ret) {
            System.out.println(s.toString());
        }
    }
}