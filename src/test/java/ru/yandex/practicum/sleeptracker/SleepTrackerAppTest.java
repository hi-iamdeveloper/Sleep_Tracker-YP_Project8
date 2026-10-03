package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SleepTrackerAppTest {

    private static final DateTimeFormatter F =
            DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private static SleepingSession session(String start, String end, SleepingQuality q) {
        return new SleepingSession(
                LocalDateTime.parse(start, F),
                LocalDateTime.parse(end, F),
                q
        );
    }

    @Test
    void countSessions_shouldReturnZero_whenListIsEmpty() {
        assertEquals(0, SleepTrackerApp.countSessions(List.of()));
    }

    @Test
    void countSessions_shouldReturnNumberOfSessions() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 23:15", "02.10.25 07:30", SleepingQuality.GOOD),
                session("02.10.25 23:50", "03.10.25 06:40", SleepingQuality.NORMAL),
                session("03.10.25 23:40", "04.10.25 08:00", SleepingQuality.BAD)
        );

        assertEquals(3, SleepTrackerApp.countSessions(sessions));
    }

    @Test
    void sleepDuration_shouldReturnDurationInMinutes() {
        SleepingSession session = session(
                "01.10.25 23:15", "02.10.25 07:30", SleepingQuality.GOOD);

        assertEquals(495, SleepTrackerApp.sleepDuration(session));
    }

    @Test
    void sleepDuration_shouldHandleShortSession() {
        SleepingSession session = session(
                "03.10.25 14:10", "03.10.25 15:00", SleepingQuality.NORMAL);

        assertEquals(50, SleepTrackerApp.sleepDuration(session));
    }

    @Test
    void minSleepDuration_shouldReturnShortestSession() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 23:15", "02.10.25 07:30", SleepingQuality.GOOD),
                session("03.10.25 14:10", "03.10.25 15:00", SleepingQuality.NORMAL),
                session("05.10.25 00:10", "05.10.25 06:20", SleepingQuality.GOOD)
        );

        assertEquals(50, SleepTrackerApp.minSleepDuration(sessions));
    }

    @Test
    void minSleepDuration_shouldThrow_whenListIsEmpty() {
        assertThrows(IllegalStateException.class,
                () -> SleepTrackerApp.minSleepDuration(List.of()));
    }

    @Test
    void maxSleepDuration_shouldReturnLongestSession() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 23:15", "02.10.25 07:30", SleepingQuality.GOOD),
                session("03.10.25 14:10", "03.10.25 15:00", SleepingQuality.NORMAL),
                session("05.10.25 00:10", "05.10.25 06:20", SleepingQuality.GOOD)
        );

        assertEquals(495, SleepTrackerApp.maxSleepDuration(sessions));
    }

    @Test
    void maxSleepDuration_shouldThrow_whenListIsEmpty() {
        assertThrows(IllegalStateException.class,
                () -> SleepTrackerApp.maxSleepDuration(List.of()));
    }

    @Test
    void averageSleepDuration_shouldReturnAverage() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 23:15", "02.10.25 07:30", SleepingQuality.GOOD),
                session("03.10.25 14:10", "03.10.25 15:00", SleepingQuality.NORMAL),
                session("05.10.25 00:10", "05.10.25 06:20", SleepingQuality.GOOD)
        );

        assertEquals(305.0, SleepTrackerApp.averageSleepDuration(sessions), 0.0001);
    }

    @Test
    void averageSleepDuration_shouldThrow_whenListIsEmpty() {
        assertThrows(IllegalStateException.class,
                () -> SleepTrackerApp.averageSleepDuration(List.of()));
    }

    @Test
    void countBadQualitySessions_shouldReturnZero_whenNoBadSessions() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 23:15", "02.10.25 07:30", SleepingQuality.GOOD),
                session("02.10.25 23:50", "03.10.25 06:40", SleepingQuality.NORMAL)
        );

        assertEquals(0, SleepTrackerApp.countBadQualitySessions(sessions));
    }

    @Test
    void countBadQualitySessions_shouldReturnNumberOfBadSessions() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 23:15", "02.10.25 07:30", SleepingQuality.GOOD),
                session("03.10.25 23:40", "04.10.25 08:00", SleepingQuality.BAD),
                session("11.10.25 23:10", "12.10.25 07:00", SleepingQuality.BAD),
                session("05.10.25 13:30", "05.10.25 14:15", SleepingQuality.NORMAL)
        );

        assertEquals(2, SleepTrackerApp.countBadQualitySessions(sessions));
    }
}