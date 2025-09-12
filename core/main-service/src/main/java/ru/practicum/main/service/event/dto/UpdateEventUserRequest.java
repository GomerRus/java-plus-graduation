package ru.practicum.main.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.service.event.model.Location;
import ru.practicum.main.service.validator.SizeAfterTrim;

import java.time.LocalDateTime;

import static ru.practicum.main.service.Constants.DATE_PATTERN;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    @SizeAfterTrim(min = 20, max = 2000)
    String annotation;

    Long category;

    @SizeAfterTrim(min = 20, max = 7000)
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    LocalDateTime eventDate;

    Location location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;

    StateAction stateAction;

    @SizeAfterTrim(min = 3, max = 120)
    String title;

    public enum StateAction {
        SEND_TO_REVIEW, CANCEL_REVIEW
    }

    public boolean hasEventDate() {
        return eventDate != null;
    }


    public boolean hasStateAction() {
        return stateAction != null;
    }
}
