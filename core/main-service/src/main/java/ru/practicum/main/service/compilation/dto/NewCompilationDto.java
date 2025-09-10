package ru.practicum.main.service.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.service.validator.SizeAfterTrim;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    @NotBlank
    @SizeAfterTrim(min = 1, max = 50)
    String title;

    Boolean pinned = false;

    Set<Long> events = new HashSet<>();
}
