package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;

public class SleepingSession {

    private LocalDateTime start;
    private LocalDateTime end;

    private SleepingQuality quality;

    public SleepingSession(LocalDateTime start, LocalDateTime end, SleepingQuality quality) {
        this.start = start;
        this.end = end;
        this.quality = quality;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public SleepingQuality getQuality() {
        return quality;
    }

}
