package ru.yandex.practicum.events.event.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.yandex.practicum.client.RecommendationClientGrpc;
import ru.yandex.practicum.interaction.dto.comment.GetCommentDto;
import ru.yandex.practicum.interaction.dto.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.EventShortDto;
import ru.yandex.practicum.interaction.dto.event.ResponseEvent;
import ru.yandex.practicum.interaction.request.ConfirmedRequestsDto;
import ru.yandex.practicum.interaction.dto.user.UserDto;
import ru.yandex.practicum.interaction.dto.user.UserShortDto;
import ru.yandex.practicum.interaction.enums.request.RequestStatus;
import ru.yandex.practicum.events.event.mapper.MapperEvent;
import ru.yandex.practicum.events.event.model.Event;
import ru.yandex.practicum.interaction.feign.client.CommentFeignClient;
import ru.yandex.practicum.interaction.feign.client.RequestFeignClient;
import ru.yandex.practicum.interaction.feign.client.UserFeignClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseEventBuilder {
    private final MapperEvent eventMapper;
    private final RequestFeignClient requestFeignClient;
    private final CommentFeignClient commentFeignClient;
    private final UserFeignClient userFeignClient;
    private final RecommendationClientGrpc recommendationClientGrpc;

    public <T extends ResponseEvent> T buildOneEventResponseDto(Event event, Class<T> type) {
        T dto;
        UserDto user = userFeignClient.getUserById(event.getInitiatorId());
        UserShortDto initiator = new UserShortDto();
        initiator.setId(user.getId());
        initiator.setName(user.getName());
        if (type == EventFullDto.class) {
            EventFullDto dtoTemp = eventMapper.toEventFullDto(event);
            dtoTemp.setInitiator(initiator);
            dtoTemp.setRating(getEventRating(event.getId()));
            dto = type.cast(dtoTemp);
        } else {
            EventShortDto dtoTemp = eventMapper.toEventShortDto(event);
            dtoTemp.setInitiator(initiator);
            dtoTemp.setRating(getEventRating(event.getId()));
            dto = type.cast(dtoTemp);
        }

        long eventId = event.getId();
        LocalDateTime created = event.getCreatedOn();

        dto.setConfirmedRequests(getOneEventConfirmedRequests(eventId));
        dto.setComments(getOneEventComments(eventId));
        return dto;
    }

    public <T extends ResponseEvent> List<T> buildManyEventResponseDto(List<Event> events, Class<T> type) {
        Map<Long, T> dtoById = new HashMap<>();
        for (Event event : events) {
            UserDto initiator = userFeignClient.getUserById(event.getInitiatorId());
            if (type == EventFullDto.class) {
                EventFullDto dtoTemp = eventMapper.toEventFullDto(event);
                dtoTemp.setInitiator(new UserShortDto(initiator.getId(), initiator.getName()));
                dtoTemp.setRating(getEventRating(event.getId()));
                dtoById.put(event.getId(), type.cast(dtoTemp));
            } else {
                EventShortDto dtoTemp = eventMapper.toEventShortDto(event);
                dtoTemp.setInitiator(new UserShortDto(initiator.getId(), initiator.getName()));
                dtoTemp.setRating(getEventRating(event.getId()));
                dtoById.put(event.getId(), type.cast(dtoTemp));
            }
        }
        getManyEventsConfirmedRequests(dtoById.keySet()).forEach(req ->
                dtoById.get(req.eventId()).setConfirmedRequests(req.countRequests()));

        getManyEventsComments(dtoById.keySet()).forEach(comment -> {
            T t = dtoById.get(comment);

            if (t.getComments() == null) {
                t.setComments(new ArrayList<>());
            }
            t.getComments().add(comment);
        });

        return new ArrayList<>(dtoById.values());
    }

    private int getOneEventConfirmedRequests(long eventId) {
        return requestFeignClient.getRequestsCountByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private List<GetCommentDto> getOneEventComments(long eventId) {
        List<GetCommentDto> comments = commentFeignClient.getCommentsByEventId(eventId);
        return comments == null ? new ArrayList<>() : comments;
    }

    private List<ConfirmedRequestsDto> getManyEventsConfirmedRequests(Collection<Long> eventIds) {
        List<ConfirmedRequestsDto> requests = requestFeignClient.getConfirmedRequestsByEventId(eventIds);
        return requests == null ? new ArrayList<>() : requests;
    }

    private List<GetCommentDto> getManyEventsComments(Set<Long> eventsIds) {
        return commentFeignClient.getLastCommentsForEvents(eventsIds);
    }

    private double getEventRating(long eventId) {
        return recommendationClientGrpc.getInteractionsCount(List.of(eventId))
                .mapToDouble(RecommendedEventProto::getScore)
                .findFirst()
                .orElse(0.0);
    }
}
