package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SleepTrackerApp {

    static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static void main(String[] args) {

        SleepLogProvider sleepLogProvider;

        try {
            sleepLogProvider = new SleepLogProvider("src/main/resources/sleep_log.txt");
        } catch (SleepLogIsMissing e) {
            System.err.println("Ошибка: " + e.getMessage() + " '" + e.getPath() + "'");
            return;
        } catch (SleepLogIsEmpty e) {
            System.err.println("Ошибка: " + e.getMessage() + " '" + e.getPath() + "'");
            return;
        }

        List<SleepingSession> sleepingSessions = sleepingSessionsParsing(sleepLogProvider);

        System.out.println("Всего сессий сна: " + countSessions(sleepingSessions));

    }

    private static ArrayList<SleepingSession> sleepingSessionsParsing(SleepLogProvider sleepLogProvider) {

        return sleepLogProvider.getSleepLog().stream()
                .map(SleepTrackerApp::lineParse)
                .collect(Collectors.toCollection(ArrayList::new));

    }

    private static SleepingSession lineParse(String line) {

        String[] p = line.split(";");

        return new SleepingSession(
                LocalDateTime.parse(p[0], FORMATTER),
                LocalDateTime.parse(p[1], FORMATTER),
                SleepingQuality.valueOf(p[2]));
    }

    public static int countSessions(List<SleepingSession> sessions) {
        return sessions.size();
    }

}

