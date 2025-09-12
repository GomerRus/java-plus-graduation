package ru.practicum.main.service.comment.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.main.service.comment.dto.GetCommentDto;
import ru.practicum.main.service.comment.enums.CommentSortType;
import ru.practicum.main.service.comment.service.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
@Slf4j
@Validated
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
