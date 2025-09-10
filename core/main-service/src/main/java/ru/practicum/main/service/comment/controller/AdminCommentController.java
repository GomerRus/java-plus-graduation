package ru.practicum.main.service.comment.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.service.comment.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/admin/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class AdminCommentController {
    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@Positive @PathVariable Long eventId,
                              @Positive @PathVariable Long commentId) {
        log.info("DELETE /admin/events/{}/comments/{}", eventId, commentId);
        commentService.deleteCommentAdmin(eventId, commentId);
        log.info("Был успешно удалён комментарий event id = {}, comment id = {}", eventId, commentId);
    }
}
