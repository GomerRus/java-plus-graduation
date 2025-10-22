package ru.yandex.practicum.request.controller;

import feign.Retryer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.request.ConfirmedRequestsDto;
import ru.yandex.practicum.interaction.request.ParticipationRequestDto;
import ru.yandex.practicum.interaction.enums.request.RequestStatus;
import ru.yandex.practicum.interaction.feign.api.RequestInternalApi;
import ru.yandex.practicum.request.service.RequestService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
public class InternalRequestController implements RequestInternalApi {

    private final RequestService requestService;

    private final Retryer retryer;

    @GetMapping("/event/{eventId}")
    public List<ParticipationRequestDto> getRequestsByEventId(@PathVariable Long eventId) {
        log.info("Пришел GET запрос на /utility/requests/event/{}", eventId);
        List<ParticipationRequestDto> requests = requestService.getRequestsByEventId(eventId);
        log.info("Получен список заявок на мероприятие с id {}: {}", eventId, requests);
        return requests;
    }

    @GetMapping("/event/{eventId}/count")
    public int getRequestsCountByEventIdAndStatus(@PathVariable Long eventId, @RequestParam(name = "status") RequestStatus status) {
        log.info("Пришел GET апрос на /utility/requests/event/{}/count с телом: {}", eventId, status);
        int count = requestService.getRequestsCountByEventIdAndStatus(eventId, status);
        log.info("Для мероприятия с id {} найдено {} заявок со статусом {}", eventId, count, status);
        return count;
    }

    @GetMapping("/confirmed")
    public List<ConfirmedRequestsDto> getConfirmedRequestsByEventId(@RequestParam(name = "ids") Collection<Long> eventsIds) {
        log.info("Пришел GET запрос на /utility/requests/confirmed с телом: {}", eventsIds);
        List<ConfirmedRequestsDto> confirmedRequests = requestService.getConfirmedRequestsByEventId(eventsIds);
        log.info("Для мероприятий с id ({}) нашлись комментарии: одобренные заявки: {}", eventsIds, confirmedRequests);
        return confirmedRequests;
    }

    @PutMapping("/{requestId}/confirm")
    public ParticipationRequestDto updateRequestStatus(@PathVariable Long requestId, @RequestBody RequestStatus status) {
        log.info("Пришел PATCH запрос на /utility/requests/{}/confirm?status={}", requestId, status);
        ParticipationRequestDto request = requestService.changeRequestStatus(requestId, status);
        log.info("Статус заявки с id {} изменен на {}", requestId, request.getStatus());
        return request;
    }

    @GetMapping("/users/{userId}/event/{eventId}")
    public ParticipationRequestDto getUserEventRequest(@PathVariable(name = "userId") Long userId, @PathVariable(name = "eventId") Long eventId) {
        log.info("Пришел GET запрос на /utility/requests/users/{}/event/{}", userId, eventId);
        ParticipationRequestDto request = requestService.getUserEventRequest(userId, eventId);
        log.info("Отправлен ответ на запрос GET /utility/requests/users/{}/event/{} с телом: {}", userId, eventId, request);
        return request;
    }
}

