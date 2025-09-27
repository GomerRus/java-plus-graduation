package ru.yandex.practicum.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.comment.GetCommentDto;
import ru.yandex.practicum.enums.comment.CommentSortType;
import ru.yandex.practicum.service.CommentService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/events/{eventId}")
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<GetCommentDto> getComment(@PathVariable @Positive final Long eventId,
                                                    @PathVariable @Positive final Long commentId) {
        log.info("Получение комментария по id и eventId : {}, {}", commentId, eventId);
        GetCommentDto comment = commentService.getCommentById(eventId, commentId);
        log.info("Отдан комментарий с телом {}", comment);
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<GetCommentDto>> getComments(@PathVariable @Positive final Long eventId,
                                                           @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                           @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                                           @RequestParam(defaultValue = "COMMENTS_NEW") CommentSortType sort) {
        log.info("Получение комментариев на мероприятие {}", eventId);
        List<GetCommentDto> comments = commentService.getEventComments(eventId, from, size, sort);
        log.info("Сформирован ответ с телом: {}", comments);
        return ResponseEntity.ok(comments);
    }
}
