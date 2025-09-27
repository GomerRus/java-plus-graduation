package ru.yandex.practicum.event.util;

import client.StatsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.comment.GetCommentDto;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.event.EventShortDto;
import ru.yandex.practicum.dto.event.ResponseEvent;
import ru.yandex.practicum.dto.request.ConfirmedRequestsDto;
import ru.yandex.practicum.dto.user.UserDto;
import ru.yandex.practicum.dto.user.UserShortDto;
import ru.yandex.practicum.enums.request.RequestStatus;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.yandex.practicum.event.mapper.MapperEvent;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.feign.client.CommentFeignClient;
import ru.yandex.practicum.feign.client.RequestFeignClient;
import ru.yandex.practicum.feign.client.UserFeignClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.yandex.practicum.utility.Constants.MIN_START_DATE;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseEventBuilder {
    private final MapperEvent eventMapper;
    private final RequestFeignClient requestFeignClient;
    private final CommentFeignClient commentFeignClient;
    private final UserFeignClient userFeignClient;
    private final StatsClient statsClient;

    public <T extends ResponseEvent> T buildOneEventResponseDto(Event event, Class<T> type) {
        T dto;
        UserDto user = userFeignClient.getUserById(event.getInitiatorId());
        UserShortDto initiator = new UserShortDto();
        initiator.setId(user.getId());
        initiator.setName(user.getName());
        if (type == EventFullDto.class) {
            EventFullDto dtoTemp = eventMapper.toEventFullDto(event);
            dtoTemp.setInitiator(initiator);
            dto = type.cast(dtoTemp);
        } else {
            EventShortDto dtoTemp = eventMapper.toEventShortDto(event);
            dtoTemp.setInitiator(initiator);
            dto = type.cast(dtoTemp);
        }

        long eventId = event.getId();
        LocalDateTime created = event.getCreatedOn();

        dto.setConfirmedRequests(getOneEventConfirmedRequests(eventId));
        dto.setViews(getOneEventViews(created, eventId));
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
                dtoById.put(event.getId(), type.cast(dtoTemp));
            } else {
                EventShortDto dtoTemp = eventMapper.toEventShortDto(event);
                dtoTemp.setInitiator(new UserShortDto(initiator.getId(), initiator.getName()));
                dtoById.put(event.getId(), type.cast(dtoTemp));
            }
        }

        getManyEventsConfirmedRequests(dtoById.keySet()).forEach(req ->
                dtoById.get(req.eventId()).setConfirmedRequests(req.countRequests()));


        getManyEventsViews(dtoById.keySet()).forEach(stats -> {
            Long id = Long.parseLong(stats.getUri().replace("/events/", ""));
            dtoById.get(id).setViews(stats.getHits());
        });

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

    private long getOneEventViews(LocalDateTime created, long eventId) {
        List<ViewStatsDto> viewStats = statsClient.getStats(created.minusMinutes(1), LocalDateTime.now().plusMinutes(1), List.of("/events/" + eventId), true);
        return viewStats == null || viewStats.isEmpty() ? 0 : viewStats.getFirst().getHits();
    }

    private List<GetCommentDto> getOneEventComments(long eventId) {
        List<GetCommentDto> comments = commentFeignClient.getCommentsByEventId(eventId);
        return comments == null ? new ArrayList<>() : comments;
    }

    private List<ConfirmedRequestsDto> getManyEventsConfirmedRequests(Collection<Long> eventIds) {
        List<ConfirmedRequestsDto> requests = requestFeignClient.getConfirmedRequestsByEventId(eventIds);
        return requests == null ? new ArrayList<>() : requests;
    }

    private List<ViewStatsDto> getManyEventsViews(Collection<Long> eventIds) {
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .toList();

        return statsClient.getStats(MIN_START_DATE, LocalDateTime.now().plusMinutes(1), uris, true);
    }

    private List<GetCommentDto> getManyEventsComments(Set<Long> eventsIds) {
        return commentFeignClient.getLastCommentsForEvents(eventsIds);
    }
}
