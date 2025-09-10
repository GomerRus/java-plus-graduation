package ru.practicum.main.service.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DuplicateException extends RuntimeException {
    ApiError error;

    public DuplicateException(String message) {
        super(message);
        error = new ApiError(
                message,
                "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }
}
