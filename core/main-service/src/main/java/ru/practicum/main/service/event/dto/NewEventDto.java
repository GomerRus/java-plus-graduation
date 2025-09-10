package ru.practicum.main.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.service.event.enums.EventState;
import ru.practicum.main.service.event.model.Location;
import ru.practicum.main.service.validator.SizeAfterTrim;

import java.time.LocalDateTime;

import static ru.practicum.main.service.Constants.DATE_PATTERN;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    @NotBlank
    @SizeAfterTrim(min = 20, max = 2000)
    String annotation;

    @NotNull
    Long category;

    @NotBlank
    @SizeAfterTrim(min = 20, max = 7000)
    String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    LocalDateTime eventDate;

    @NotNull
    Location location;

    Boolean paid = false;

    @PositiveOrZero
    Integer participantLimit = 0;

    Boolean requestModeration = true;

    @NotBlank
    @SizeAfterTrim(min = 3, max = 120)
    String title;

    @JsonIgnore
    LocalDateTime createdOn = LocalDateTime.now();

    @JsonIgnore
    EventState state = EventState.PENDING;
}
