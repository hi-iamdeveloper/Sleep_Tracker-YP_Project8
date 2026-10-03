package ru.yandex.practicum.sleeptracker;

public class SleepLogIsEmpty extends Exception {

    private final String path;

    public SleepLogIsEmpty(String path, String message) {
        super(message);
        this.path = path;
    }

    public SleepLogIsEmpty(String path, String message, Throwable cause) {
        super(message, cause);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}