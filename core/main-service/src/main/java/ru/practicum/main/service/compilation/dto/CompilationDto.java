package ru.practicum.main.service.compilation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.service.event.dto.EventShortDto;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    @Min(1)
    @NotNull
    Long id;

    @NotNull
    Boolean pinned;

    @NotBlank
    String title;

    Set<EventShortDto> events;
}
