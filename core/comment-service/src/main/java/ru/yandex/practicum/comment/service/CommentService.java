package ru.yandex.practicum.comment.service;

import ru.yandex.practicum.interaction.dto.comment.CommentDto;
import ru.yandex.practicum.interaction.dto.comment.GetCommentDto;
import ru.yandex.practicum.interaction.enums.comment.CommentSortType;

import java.util.List;
import java.util.Set;

public interface CommentService {
    GetCommentDto addNewComment(Long userId, Long eventId, CommentDto commentDto);

    GetCommentDto updateComment(Long userId, Long eventId, Long commentId, CommentDto commentDto);

    void deleteCommentPrivate(Long userId, Long eventId, Long commentId);

    void deleteCommentAdmin(Long eventId, Long commentId);

    GetCommentDto getCommentById(Long eventId, Long commentId);

    List<GetCommentDto> getEventComments(Long eventId, Integer from, Integer size, CommentSortType sort);

    List<GetCommentDto> getCommentsByEventsIds(Set<Long> eventsId);
}
