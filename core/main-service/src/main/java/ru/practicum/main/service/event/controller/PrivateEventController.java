package ru.practicum.main.service.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.service.event.dto.*;
import ru.practicum.main.service.event.service.EventService;
import ru.practicum.main.service.request.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllUserEvents(@PathVariable(name = "userId") Long userId,
                                                                @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                                @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("Пришел GET запрос на /users/{}/events", userId);
        List<EventShortDto> events = eventService.getAllUsersEvents(userId, PageRequest.of(from, size));
        log.info("Отправлен ответ GET /users/{}/events с телом: {}", userId, events);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> addNewEvent(@PathVariable(name = "userId") Long userId,
                                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Пришел POST запрос на /users/{}/events с телом: {}", userId, newEventDto);
        EventFullDto event = eventService.addNewEvent(userId, newEventDto);
        log.info("Отправлен ответ POST /users/{}/events с телом: {}", userId, event);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable(name = "userId") Long userId,
                                                     @PathVariable(name = "eventId") Long eventId) {
        log.info("Пришел GET запрос на /users/{}/events/{}", userId, eventId);
        EventFullDto event = eventService.getEventForUser(userId, eventId);
        log.info("Отправлен ответ на GET /users/{}/events/{} с телом: {}", userId, eventId, event);
        return ResponseEntity.ok(event);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable(name = "userId") Long userId,
                                                    @PathVariable(name = "eventId") Long eventId,
                                                    @Valid @RequestBody UpdateEventUserRequest eventDto) {
        log.info("Пришел PATCH запрос на /users/{}/events/{} с телом: {}", userId, eventId, eventDto);
        EventFullDto event = eventService.updateEventByUser(userId, eventId, eventDto);
        log.info("Отправлен ответ на PATCH /users/{}/events/{} с телом: {}", userId, eventId, event);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(@PathVariable(name = "userId") Long userId,
                                                                          @PathVariable(name = "eventId") Long eventId) {
        log.info("Пришел GET запрос на /users/{}/events/{}/requests", userId, eventId);
        List<ParticipationRequestDto> requests = eventService.getEventRequests(userId, eventId);
        log.info("Отправлен ответ на GET /users/{}/events/{}/requests с телом: {}", userId, eventId, requests);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequests(@PathVariable(name = "userId") Long userId,
                                                                         @PathVariable(name = "eventId") Long eventId,
                                                                         @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Пришел PATCH запрос на /users/{}/events/{}/requests с телом: {}", userId, eventId, updateRequest);
        EventRequestStatusUpdateResult result = eventService.updateEventRequests(userId, eventId, updateRequest);
        log.info("Отправлен ответ на PATCH /users/{}/events/{},requests с телом: {}", userId, eventId, result);
        return ResponseEntity.ok(result);
    }
}
