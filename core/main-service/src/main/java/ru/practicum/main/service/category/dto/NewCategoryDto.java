package ru.practicum.main.service.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.service.validator.SizeAfterTrim;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryDto {
    @NotBlank
    @SizeAfterTrim(min = 1, max = 50)
    String name;
}
