package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.nio.file.*;

public class SleepLogProvider {

    private final String PATH;
    List<String> sleepLog;

    public SleepLogProvider(String PATH) throws SleepLogIsMissing, SleepLogIsEmpty {
        this.PATH = PATH;
        Path path = Path.of(PATH);

        try {
            sleepLog = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SleepLogIsMissing(PATH, "Не удалось прочитать файл: ");
        }

        if (sleepLog.isEmpty()) {
            throw new SleepLogIsEmpty(PATH, "Файл пуст:");
        }
    }

    public List<String> getSleepLog() {
        return sleepLog;
    }

    @Override
    public String toString() {
        return "SleepLogProvider{" +
                "sleepLog=" + sleepLog +
                '}';
    }
}
