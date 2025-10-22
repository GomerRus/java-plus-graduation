package ru.yandex.practicum.interaction.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.interaction.validator.SizeAfterTrim;

@Getter
@Setter
public class NewCategoryDto {
    @NotBlank
    @SizeAfterTrim(min = 1, max = 50)
    private String name;
}
