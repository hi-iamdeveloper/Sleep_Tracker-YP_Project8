package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SleepTrackerAppTest {

    @Test
    void countSessions_shouldReturnZero_whenListIsEmpty() {
        assertEquals(0, SleepTrackerApp.countSessions(List.of()));
    }

    @Test
    void countSessions_shouldReturnSize_whenListHasSessions() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.now(), LocalDateTime.now().plusHours(8), SleepingQuality.GOOD),
                new SleepingSession(LocalDateTime.now(), LocalDateTime.now().plusHours(7), SleepingQuality.NORMAL),
                new SleepingSession(LocalDateTime.now(), LocalDateTime.now().plusHours(6), SleepingQuality.BAD)
        );
        assertEquals(3, SleepTrackerApp.countSessions(sessions));
    }
}