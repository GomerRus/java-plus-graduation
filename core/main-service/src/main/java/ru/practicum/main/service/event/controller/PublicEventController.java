package ru.practicum.main.service.event.controller;

import client.StatsClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.main.service.event.dto.EventFullDto;
import ru.practicum.main.service.event.dto.EventShortDto;
import ru.practicum.main.service.event.enums.EventSortType;
import ru.practicum.main.service.event.service.EventService;
import ru.practicum.main.service.event.service.param.GetEventUserParam;
import ru.practicum.main.service.exception.BadRequestException;
import ru.practicum.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.main.service.Constants.DATE_PATTERN;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
public class PublicEventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsByFilters(@RequestParam(name = "text", required = false) String text,
                                                                  @RequestParam(name = "categories", required = false) List<Long> categories,
                                                                  @RequestParam(name = "paid", required = false) Boolean paid,
                                                                  @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeStart,
                                                                  @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeEnd,
                                                                  @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                                  @RequestParam(name = "sort", required = false) EventSortType sort,
                                                                  @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                                  @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size,
                                                                  HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("rangeStart > rangeEnd");
        }
        log.info("Пришел GET запрос /events на Public Event Controller");
        doHit(request);

        Pageable page;
        if (sort != null) {
            Sort sortType = switch (sort) {
                case EVENT_DATE -> Sort.by("createdOn").ascending();
                case VIEWS -> Sort.by("views").ascending();
            };
            page = PageRequest.of(from, size, sortType);
        } else {
            page = PageRequest.of(from, size);
        }

        GetEventUserParam param = GetEventUserParam.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .page(page)
                .build();

        List<EventShortDto> events = eventService.getEventsByUser(param);
        log.info("Отправлен ответ на GET /events Public Event Controller с телом: {}", events);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Пришел GET запрос на /events/{} Public Event Controller", eventId);
        doHit(request);
        EventFullDto event = eventService.getEventById(eventId);
        log.info("Отправлен ответ на GET /events/{} c телом: {}", eventId, event);
        return ResponseEntity.ok(event);
    }

    private void doHit(HttpServletRequest request) {
        EndpointHitDto hitDto = new EndpointHitDto();
        hitDto.setApp("ewm-main-service");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setCreated(LocalDateTime.now());
        statsClient.createHit(hitDto);
    }
}
