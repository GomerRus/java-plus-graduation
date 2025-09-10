package ru.practicum.main.service.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.service.event.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventParam {
    String title;

    String description;

    String annotation;

    Long category;

    LocalDateTime createdOn;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    Location location;

    public boolean hasAnnotation() {
        return annotation != null;
    }

    public boolean hasCategory() {
        return category != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public boolean hasPaid() {
        return paid != null;
    }

    public boolean hasParticipantLimit() {
        return participantLimit != null;
    }

    public boolean hasRequestModeration() {
        return requestModeration != null;
    }

    public boolean hasTitle() {
        return title != null;
    }
}
