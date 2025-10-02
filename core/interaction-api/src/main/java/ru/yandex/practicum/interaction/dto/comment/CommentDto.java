package ru.yandex.practicum.interaction.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.interaction.validator.SizeAfterTrim;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {

    @NotBlank
    @SizeAfterTrim(min = 1, max = 5000)
    String text;
}
