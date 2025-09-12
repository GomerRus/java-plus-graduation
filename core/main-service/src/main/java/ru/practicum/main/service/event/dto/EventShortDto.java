package ru.practicum.main.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.service.category.dto.CategoryDto;
import ru.practicum.main.service.comment.dto.GetCommentDto;
import ru.practicum.main.service.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.main.service.Constants.DATE_PATTERN;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto implements ResponseEvent {
    @NotBlank
    String annotation;

    @NotNull
    CategoryDto category;

    int confirmedRequests;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    LocalDateTime eventDate;

    Long id;

    @NotNull
    UserShortDto initiator;

    @NotNull
    Boolean paid;

    @NotBlank
    String title;

    long views;

    @JsonIgnore
    int participantLimit;

    List<GetCommentDto> comments = new ArrayList<>();
}
