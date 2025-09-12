package ru.practicum.main.service.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotFoundException extends RuntimeException {
    ApiError error;

    public NotFoundException(String message) {
        super(message);
        error = new ApiError(
                message,
                "The required object was not found.",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());
    }
}
