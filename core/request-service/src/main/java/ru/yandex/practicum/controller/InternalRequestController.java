package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.request.ConfirmedRequestsDto;
import ru.yandex.practicum.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.enums.request.RequestStatus;
import ru.yandex.practicum.feign.api.RequestInternalApi;
import ru.yandex.practicum.service.RequestService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
public class InternalRequestController implements RequestInternalApi {

    private final RequestService requestService;

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
}
