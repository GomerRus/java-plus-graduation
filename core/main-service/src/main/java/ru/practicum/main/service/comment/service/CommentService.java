package ru.practicum.main.service.comment.service;

import ru.practicum.main.service.comment.dto.CommentDto;
import ru.practicum.main.service.comment.dto.GetCommentDto;
import ru.practicum.main.service.comment.enums.CommentSortType;

import java.util.List;

public interface CommentService {
    GetCommentDto addNewComment(Long userId, Long eventId, CommentDto commentDto);

    GetCommentDto updateComment(Long userId, Long eventId, Long commentId, CommentDto commentDto);

    void deleteCommentPrivate(Long userId, Long eventId, Long commentId);

    void deleteCommentAdmin(Long eventId, Long commentId);

    GetCommentDto getCommentById(Long eventId, Long commentId);

    List<GetCommentDto> getEventComments(Long eventId, Integer from, Integer size, CommentSortType sort);
}
