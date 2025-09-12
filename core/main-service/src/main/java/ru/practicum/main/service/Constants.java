package ru.practicum.main.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

public interface Constants {
    String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String DUPLICATE_USER = "Пользователь с таким Email уже существует";
    String USER_NOT_FOUND = "Пользователь не найден";
    String EVENT_NOT_FOUND = "Событие не найдено";
    String CATEGORY_NOT_FOUND = "Категория не найдена";
    String COMMENT_NOT_FOUND = "Комментарий не найден";
    String COMMENT_EVENT_NOT_MATCH = "Мероприятие к которому был написан комментарий не совпадает с указанным в запросе";
    String COMMENT_AUTHOR_NOT_MATCH = "Автор комментария не совпадает с пользователем, указанным в запросе";
    LocalDateTime MIN_START_DATE = LocalDateTime.of(1970, 1, 1, 0, 0);
    Pageable DEFAULT_COMMENTS = PageRequest.of(0, 10, Sort.by("created").descending());
}
