package ru.yandex.practicum.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalServerException extends RuntimeException {
    ApiError error;

    public InternalServerException(String message) {
        super(message);
        error = new ApiError(
                message,
                "Server error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now());
    }
}