package com.shrill.example;

public class Student {

    public enum Type { FEMALE, MALE, OTHER }

    private final String name;
    private final int score;
    private final Type type;

    public Student(String name, int score, Type type) {
        this.name = name;
        this.score = score;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }
}
