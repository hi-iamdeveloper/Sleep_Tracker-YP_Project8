package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SleepTrackerApp {

    static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private static final LocalTime NOON = LocalTime.of(12, 0);
    private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

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
        System.out.println("Максимальная сессия сна: " + maxSleepDuration(sleepingSessions) + " минут");
        System.out.println("Минимальная сессия сна: " + minSleepDuration(sleepingSessions) + " минут");
        System.out.println("Средняя продолжительность сна: " + averageSleepDuration(sleepingSessions) + " минут");
        System.out.println("Количество ночей с плохим сном: " + countBadQualitySessions(sleepingSessions));
        System.out.println("Количество бессоных ночей: " + countSleeplessNights(sleepingSessions));
        System.out.println("Вы: " + detectChronotype(sleepingSessions));

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

    public static long sleepDuration(SleepingSession sleepingSession) {

        return Duration.between(sleepingSession.getStart(), sleepingSession.getEnd()).toMinutes();

    }

    public static long minSleepDuration(List<SleepingSession> sessions) {
        return sessions.stream()
                .mapToLong(SleepTrackerApp::sleepDuration)
                .min()
                .orElseThrow(() -> new IllegalStateException("Список сессий пуст"));
    }

    public static long maxSleepDuration(List<SleepingSession> sessions) {
        return sessions.stream()
                .mapToLong(SleepTrackerApp::sleepDuration)
                .max()
                .orElseThrow(() -> new IllegalStateException("Список сессий пуст"));
    }

    public static int averageSleepDuration(List<SleepingSession> sessions) {
        return (int) sessions.stream()
                .mapToLong(SleepTrackerApp::sleepDuration)
                .average()
                .orElseThrow(() -> new IllegalStateException("Список сессий пуст"));
    }

    public static long countBadQualitySessions(List<SleepingSession> sessions) {
        return sessions.stream()
                .filter(s -> s.getQuality() == SleepingQuality.BAD)
                .count();
    }

    public static long countSleeplessNights(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return 0;
        }

        LocalDateTime firstStart = sessions.stream()
                .map(SleepingSession::getStart)
                .min(LocalDateTime::compareTo)
                .orElseThrow();

        LocalDateTime lastEnd = sessions.stream()
                .map(SleepingSession::getEnd)
                .max(LocalDateTime::compareTo)
                .orElseThrow();

        LocalDate firstNight = firstStart.toLocalTime().isBefore(LocalTime.NOON)
                ? firstStart.toLocalDate().minusDays(1)
                : firstStart.toLocalDate();

        LocalDate lastNight = lastEnd.toLocalDate();

        long nights = ChronoUnit.DAYS.between(firstNight, lastNight) + 1;

        return java.util.stream.LongStream.range(0, nights)
                .mapToObj(firstNight::plusDays)
                .filter(night -> sessions.stream().noneMatch(s ->
                        s.getStart().isBefore(night.atTime(6, 0))
                                && s.getEnd().isAfter(night.atStartOfDay())))
                .count();
    }

    public static Chronotype detectChronotype(List<SleepingSession> sessions) {
        long owl = sessions.stream()
                .filter(SleepTrackerApp::isNightSession)
                .filter(s -> s.getStart().toLocalTime().isAfter(LocalTime.of(23, 0))
                        && s.getEnd().toLocalTime().isAfter(LocalTime.of(9, 0)))
                .count();

        long lark = sessions.stream()
                .filter(SleepTrackerApp::isNightSession)
                .filter(s -> s.getStart().toLocalTime().isBefore(LocalTime.of(22, 0))
                        && s.getEnd().toLocalTime().isBefore(LocalTime.of(7, 0)))
                .count();

        long pigeon = sessions.stream()
                .filter(SleepTrackerApp::isNightSession)
                .count() - owl - lark;

        if (owl > lark && owl > pigeon) {
            return Chronotype.OWL;
        }
        if (lark > owl && lark > pigeon) {
            return Chronotype.LARK;
        }
        return Chronotype.PIGEON;
    }

    private static boolean isNightSession(SleepingSession session) {
        LocalDate night = session.getEnd().toLocalDate();
        return session.getStart().isBefore(night.atTime(6, 0))
                && session.getEnd().isAfter(night.atStartOfDay());
    }



}

