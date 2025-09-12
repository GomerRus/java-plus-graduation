package ru.practicum.main.service.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BadRequestException extends RuntimeException {
    ApiError error;

    public BadRequestException(String message) {
        super(message);
        error = new ApiError(
                message,
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }
}
