package ru.yandex.practicum.interaction.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.interaction.validator.SizeAfterTrim;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class NewCompilationDto {
    @NotBlank
    @SizeAfterTrim(min = 1, max = 50)
    private String title;

    private Boolean pinned = false;

    private Set<Long> events = new HashSet<>();
}
