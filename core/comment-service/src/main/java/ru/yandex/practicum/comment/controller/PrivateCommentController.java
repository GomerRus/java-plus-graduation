package ru.yandex.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.comment.CommentDto;
import ru.yandex.practicum.interaction.dto.comment.GetCommentDto;
import ru.yandex.practicum.comment.service.CommentService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/users/{userId}/events/{eventId}")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity<GetCommentDto> createComment(@PathVariable("userId") @Positive Long userId,
                                                       @PathVariable("eventId") @Positive Long eventId,
                                                       @RequestBody @Valid CommentDto commentDto) {
        log.info("Create comment for user {} event {} with body {}", userId, eventId, commentDto);
        GetCommentDto comment = commentService.addNewComment(userId, eventId, commentDto);
        log.info("Успешное создание комментария {}", comment);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<GetCommentDto> patchComment(@PathVariable("userId") @Positive Long userId,
                                                      @PathVariable("eventId") @Positive Long eventId,
                                                      @PathVariable("commentId") @Positive Long commentId,
                                                      @RequestBody @Valid CommentDto commentDto) {
        log.info("Patch comment for user {} event {} with id {} and body {}", userId, eventId, commentId, commentDto);
        GetCommentDto comment = commentService.updateComment(userId, eventId, commentId, commentDto);
        log.info("Комментарий изменен {}", comment);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("userId") @Positive Long userId,
                              @PathVariable("eventId") @Positive Long eventId,
                              @PathVariable("commentId") @Positive Long commentId) {
        log.info("Delete comment for user {} event {} with id {}", userId, eventId, commentId);
        commentService.deleteCommentPrivate(userId, eventId, commentId);
        log.info("Комментарий с id {} успешно удален", commentId);
    }
}
