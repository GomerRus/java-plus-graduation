package ru.practicum.main.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.service.category.dto.CategoryDto;
import ru.practicum.main.service.comment.dto.GetCommentDto;
import ru.practicum.main.service.event.enums.EventState;
import ru.practicum.main.service.event.model.Location;
import ru.practicum.main.service.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.main.service.Constants.DATE_PATTERN;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto implements ResponseEvent {
    Long id;

    @NotBlank
    String title;

    @NotNull
    UserShortDto initiator;

    @NotNull
    Location location;

    @NotNull
    Boolean paid;

    @NotBlank
    String annotation;

    @NotNull
    CategoryDto category;

    int confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    LocalDateTime createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    LocalDateTime publishedOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    LocalDateTime eventDate;

    int participantLimit = 0;

    String description;

    Boolean requestModeration = true;

    EventState state;

    long views;

    List<GetCommentDto> comments = new ArrayList<>();
}
