package ru.practicum.main.service.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.service.validator.SizeAfterTrim;

@Getter
@Setter
public class NewCategoryDto {
    @NotBlank
    @SizeAfterTrim(min = 1, max = 50)
    private String name;
}
