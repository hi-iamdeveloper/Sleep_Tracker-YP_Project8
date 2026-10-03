package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SleepLogProvider {

    private final String path;
    private final List<String> sleepLog;

    public SleepLogProvider(String path) throws SleepLogIsMissing, SleepLogIsEmpty {
        this.path = path;
        Path file = Path.of(path);

        if (Files.notExists(file)) {
            throw new SleepLogIsMissing(path, "Файл не найден");
        }

        try {
            this.sleepLog = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SleepLogIsMissing(path, "Не удалось прочитать файл", e);
        }

        if (sleepLog.isEmpty()) {
            throw new SleepLogIsEmpty(path, "Лог пуст");
        }
    }

    public List<String> getSleepLog() {
        return sleepLog;
    }

    public String getPath() {
        return path;
    }
}