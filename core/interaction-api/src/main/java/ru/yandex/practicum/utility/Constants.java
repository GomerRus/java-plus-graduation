package ru.yandex.practicum.utility;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

public class Constants {
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DUPLICATE_USER = "Пользователь с таким Email уже существует";
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String EVENT_NOT_FOUND = "Событие не найдено";
    public static final String CATEGORY_NOT_FOUND = "Категория не найдена";
    public static final String REQUEST_NOT_FOUND = "Заявка не найдена";
    public static final String COMMENT_NOT_FOUND = "Комментарий не найден";
    public static final String COMMENT_EVENT_NOT_MATCH = "Мероприятие к которому был написан комментарий не совпадает с указанным в запросе";
    public static final String COMMENT_AUTHOR_NOT_MATCH = "Автор комментария не совпадает с пользователем, указанным в запросе";
    public static final LocalDateTime MIN_START_DATE = LocalDateTime.of(1970, 1, 1, 0, 0);
    public static final Pageable DEFAULT_COMMENTS = PageRequest.of(0, 10, Sort.by("created").descending());
}
