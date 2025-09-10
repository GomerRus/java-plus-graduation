package ru.practicum.main.service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.service.validator.SizeAfterTrim;

@Getter
@Setter
public class NewUserRequest {

    @NotNull
    @Email
    @Size(min = 6, max = 254)
    private String email;

    @NotBlank
    @SizeAfterTrim(min = 2, max = 250)
    private String name;
}
