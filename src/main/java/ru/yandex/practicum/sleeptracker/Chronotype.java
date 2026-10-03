package ru.yandex.practicum.sleeptracker;

public enum Chronotype {
    OWL("сова"),
    LARK("жаворонок"),
    PIGEON("голубь");

    private final String russianName;

    Chronotype(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }

    @Override
    public String toString() {
        return russianName;
    }
}