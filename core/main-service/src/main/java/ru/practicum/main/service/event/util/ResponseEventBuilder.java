package ru.practicum.main.service.event.util;

import client.StatParam;
import client.StatsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.main.service.comment.CommentRepository;
import ru.practicum.main.service.comment.MapperComment;
import ru.practicum.main.service.comment.dto.GetCommentDto;
import ru.practicum.main.service.comment.model.Comment;
import ru.practicum.main.service.event.MapperEvent;
import ru.practicum.main.service.event.dto.EventFullDto;
import ru.practicum.main.service.event.dto.EventShortDto;
import ru.practicum.main.service.event.dto.ResponseEvent;
import ru.practicum.main.service.event.model.Event;
import ru.practicum.main.service.request.RequestRepository;
import ru.practicum.main.service.request.enums.RequestStatus;
import ru.practicum.main.service.request.model.ConfirmedRequests;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.practicum.main.service.Constants.DEFAULT_COMMENTS;
import static ru.practicum.main.service.Constants.MIN_START_DATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResponseEventBuilder {
    private final MapperEvent eventMapper;
    private final MapperComment commentMapper;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final StatsClient statsClient;

    public <T extends ResponseEvent> T buildOneEventResponseDto(Event event, Class<T> type) {
        T dto;

        if (type == EventFullDto.class) {
            EventFullDto dtoTemp = eventMapper.toEventFullDto(event);
            dto = type.cast(dtoTemp);
        } else {
            EventShortDto dtoTemp = eventMapper.toEventShortDto(event);
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
            if (type == EventFullDto.class) {
                EventFullDto dtoTemp = eventMapper.toEventFullDto(event);
                dtoById.put(event.getId(), type.cast(dtoTemp));
            } else {
                EventShortDto dtoTemp = eventMapper.toEventShortDto(event);
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
            T t = dtoById.get(comment.getEvent().getId());

            if (t.getComments() == null) {
                t.setComments(new ArrayList<>());
            }

            t.getComments().add(commentMapper.toGetCommentDto(comment));
        });

        return new ArrayList<>(dtoById.values());
    }

    private int getOneEventConfirmedRequests(long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private long getOneEventViews(LocalDateTime created, long eventId) {
        StatParam statParam = StatParam.builder()
                .start(created.minusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(1))
                .unique(true)
                .uris(List.of("/events/" + eventId))
                .build();

        List<ViewStatsDto> viewStats = statsClient.getStat(
                statParam.getStart(),
                statParam.getEnd(),
                statParam.getUris(),
                statParam.getUnique()
        );

        log.debug("Статистика пустая = {} . Одиночный от статистики по запросу uris = {}, start = {}, end = {}",
                viewStats.isEmpty(),
                statParam.getUris(),
                statParam.getStart(),
                statParam.getEnd());
        return viewStats.isEmpty() ? 0 : viewStats.getFirst().getHits();
    }

    private List<GetCommentDto> getOneEventComments(long eventId) {
        return commentRepository.findByEventId(eventId, DEFAULT_COMMENTS).stream()
                .map(commentMapper::toGetCommentDto)
                .toList();
    }

    private List<ConfirmedRequests> getManyEventsConfirmedRequests(Collection<Long> eventIds) {
        return requestRepository.getConfirmedRequests(eventIds, RequestStatus.CONFIRMED);
    }

    private List<ViewStatsDto> getManyEventsViews(Collection<Long> eventIds) {
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .toList();

        StatParam statParam = StatParam.builder()
                .start(MIN_START_DATE)
                .end(LocalDateTime.now().plusMinutes(1))
                .unique(true)
                .uris(uris)
                .build();

        List<ViewStatsDto> viewStats = statsClient.getStat(statParam.getStart(),
                statParam.getEnd(),
                statParam.getUris(),
                statParam.getUnique()
        );

        log.debug("Получен ответ size = {}, массовый от статистики по запросу uris = {}, start = {}, end = {}",
                viewStats.size(),
                statParam.getUris(),
                statParam.getStart(),
                statParam.getEnd());
        return viewStats;
    }

    private List<Comment> getManyEventsComments(Set<Long> eventsIds) {
        return commentRepository.findLastCommentsForManyEvents(eventsIds);
    }
}
