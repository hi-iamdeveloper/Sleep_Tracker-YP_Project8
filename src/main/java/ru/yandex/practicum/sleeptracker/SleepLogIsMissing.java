package ru.yandex.practicum.sleeptracker;

public class SleepLogIsMissing extends Exception {

    private final String path;

    public SleepLogIsMissing(String path, String message) {
        super(message);
        this.path = path;
    }

    public SleepLogIsMissing(String path, String message, Throwable cause) {
        super(message, cause);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}