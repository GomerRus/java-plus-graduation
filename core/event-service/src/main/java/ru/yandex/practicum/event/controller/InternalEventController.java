package ru.yandex.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.event.service.EventService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/events")
public class InternalEventController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventByIdWithoutHit(@PathVariable Long eventId) {
        log.info("Пришел GET запрос на /utility/events/{}", eventId);
        EventFullDto event = eventService.getEventByIdAnyState(eventId);
        log.info("Отправлен ответ на запрос GET /utility/events/{} c телом: {}", eventId, event);
        return ResponseEntity.ok(event);
    }
}
