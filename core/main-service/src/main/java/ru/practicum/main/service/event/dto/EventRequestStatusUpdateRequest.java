package ru.practicum.main.service.event.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {
    Set<Long> requestIds;

    Status status;

    public enum Status {
        CONFIRMED, REJECTED
    }
}
