package ru.practicum.main.service.event.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidatorEventTime {
    /**
     * @param time - checked time
     * @param hours - count of hours after which the date will be good
     */
    public static boolean isEventTimeBad(LocalDateTime time, int hours) {
        return time.plusHours(hours).isBefore(LocalDateTime.now());
    }
}
