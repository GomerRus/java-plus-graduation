package ru.yandex.practicum.feign.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.comment.GetCommentDto;

import java.util.List;
import java.util.Set;

public interface CommentInternalApi {
    @GetMapping("/event/last")
    List<GetCommentDto> getLastCommentsForEvents(@RequestParam(name = "ids") Set<Long> eventsId);

    @GetMapping("/event/{eventId}")
    List<GetCommentDto> getCommentsByEventId(@PathVariable Long eventId);
}
