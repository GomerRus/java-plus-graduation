package ru.yandex.practicum.event.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidatorEventTime {

    public static boolean isEventTimeBad(LocalDateTime time, int hours) {
        return time.plusHours(hours).isBefore(LocalDateTime.now());
    }
}
