package ru.yandex.practicum.interaction.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddLikeException extends RuntimeException {
    private static final String MESSAGE = "Лайк можно поставить только на мероприятие которое было посещено";
    ApiError error;

    public AddLikeException() {
        super(MESSAGE);
        error = new ApiError(
                MESSAGE,
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }
}
