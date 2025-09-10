package ru.practicum.main.service.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.main.service.comment.dto.CommentDto;
import ru.practicum.main.service.comment.dto.GetCommentDto;
import ru.practicum.main.service.comment.service.CommentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}")
@Validated
@Slf4j
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
