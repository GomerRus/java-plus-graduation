package ru.yandex.practicum.interaction.feign.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.comment.GetCommentDto;
import ru.yandex.practicum.interaction.exception.ServiceUnavailableException;
import ru.yandex.practicum.interaction.feign.api.CommentInternalApi;

import java.util.List;
import java.util.Set;

@Component
public class CommentFallbackClient implements CommentInternalApi {

    private static final String SERVICE_NAME = "comment-service";

    @Override
    public List<GetCommentDto> getLastCommentsForEvents(Set<Long> eventsId) {
        throw new ServiceUnavailableException(SERVICE_NAME);
    }

    @Override
    public List<GetCommentDto> getCommentsByEventId(Long eventId) {
        throw new ServiceUnavailableException(SERVICE_NAME);
    }
}
