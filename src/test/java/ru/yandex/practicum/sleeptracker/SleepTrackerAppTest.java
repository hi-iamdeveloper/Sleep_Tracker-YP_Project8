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

    @Test
    void countSleeplessNights_shouldCountFirstNight_whenSessionStartsInEvening() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 23:00", "02.10.25 07:00", SleepingQuality.GOOD)
        );

        assertEquals(1, SleepTrackerApp.countSleeplessNights(sessions));
    }

    @Test
    void countSleeplessNights_shouldCountGapBetweenSessions() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 23:00", "02.10.25 07:00", SleepingQuality.GOOD),
                session("05.10.25 23:00", "06.10.25 07:00", SleepingQuality.GOOD)
        );

        assertEquals(4, SleepTrackerApp.countSleeplessNights(sessions));
    }

    @Test
    void detectChronotype_shouldReturnOwl_whenMostNightsAreOwl() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 23:30", "02.10.25 10:00", SleepingQuality.GOOD),
                session("02.10.25 23:45", "03.10.25 09:30", SleepingQuality.NORMAL),
                session("03.10.25 23:10", "04.10.25 10:30", SleepingQuality.GOOD)
        );

        assertEquals(Chronotype.OWL, SleepTrackerApp.detectChronotype(sessions));
    }

    @Test
    void detectChronotype_shouldReturnLark_whenMostNightsAreLark() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 21:30", "02.10.25 06:00", SleepingQuality.GOOD),
                session("02.10.25 21:45", "03.10.25 06:30", SleepingQuality.NORMAL),
                session("03.10.25 21:00", "04.10.25 05:30", SleepingQuality.GOOD)
        );

        assertEquals(Chronotype.LARK, SleepTrackerApp.detectChronotype(sessions));
    }

    @Test
    void detectChronotype_shouldReturnPigeon_whenMostNightsArePigeon() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 22:30", "02.10.25 07:30", SleepingQuality.GOOD),
                session("02.10.25 23:00", "03.10.25 08:00", SleepingQuality.NORMAL),
                session("03.10.25 22:00", "04.10.25 07:00", SleepingQuality.GOOD)
        );

        assertEquals(Chronotype.PIGEON, SleepTrackerApp.detectChronotype(sessions));
    }

    @Test
    void detectChronotype_shouldReturnPigeon_whenListIsEmpty() {
        assertEquals(Chronotype.PIGEON, SleepTrackerApp.detectChronotype(List.of()));
    }

    @Test
    void detectChronotype_shouldReturnPigeon_whenTieBetweenOwlAndLark() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 23:30", "02.10.25 10:00", SleepingQuality.GOOD),
                session("02.10.25 21:30", "03.10.25 06:00", SleepingQuality.GOOD)
        );

        assertEquals(Chronotype.PIGEON, SleepTrackerApp.detectChronotype(sessions));
    }

    @Test
    void detectChronotype_shouldIgnoreDaySessions() {
        List<SleepingSession> sessions = List.of(
                session("01.10.25 14:00", "01.10.25 15:00", SleepingQuality.GOOD),
                session("02.10.25 13:00", "02.10.25 14:30", SleepingQuality.NORMAL)
        );

        assertEquals(Chronotype.PIGEON, SleepTrackerApp.detectChronotype(sessions));
    }
}