package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.util.List;

public class SleepingSession {

    LocalDateTime start;
    LocalDateTime end;
    SleepingQuality quality;

    public SleepingSession(LocalDateTime start, LocalDateTime end, SleepingQuality quality) {
        this.start = start;
        this.end = end;
        this.quality = quality;
    }
}
