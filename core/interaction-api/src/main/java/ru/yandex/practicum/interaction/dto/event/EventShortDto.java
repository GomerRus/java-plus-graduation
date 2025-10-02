package ru.yandex.practicum.interaction.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.interaction.dto.category.CategoryDto;
import ru.yandex.practicum.interaction.dto.comment.GetCommentDto;
import ru.yandex.practicum.interaction.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.interaction.utility.Constants.DATE_PATTERN;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    double rating;

    @JsonIgnore
    int participantLimit;

    List<GetCommentDto> comments = new ArrayList<>();
}
