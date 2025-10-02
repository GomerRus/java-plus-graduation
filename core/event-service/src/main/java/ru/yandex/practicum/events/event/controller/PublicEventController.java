package ru.yandex.practicum.events.event.controller;

import com.google.protobuf.Timestamp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.yandex.practicum.client.UserActionClientGrpc;
import ru.yandex.practicum.interaction.dto.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.EventShortDto;
import ru.yandex.practicum.interaction.enums.event.EventSortType;
import ru.yandex.practicum.interaction.exception.BadRequestException;
import ru.yandex.practicum.events.event.service.EventService;
import ru.yandex.practicum.events.event.service.param.GetEventUserParam;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static ru.yandex.practicum.interaction.utility.Constants.DATE_PATTERN;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByFilters(@RequestParam(name = "text", required = false) String text,
                                                  @RequestParam(name = "categories", required = false) List<Long> categories,
                                                  @RequestParam(name = "paid", required = false) Boolean paid,
                                                  @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeStart,
                                                  @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime rangeEnd,
                                                  @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(name = "sort", required = false) EventSortType sort,
                                                  @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("rangeStart > rangeEnd");
        }

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

        return eventService.getEventsByUser(param);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable @Positive Long eventId,
                                     @RequestHeader("X-EWM-USER-ID") @Positive Long userId) {
        log.info("Пришел GET запрос на /events/{} Public Event Controller", eventId);

        return eventService.getEventById(userId, eventId);
    }

    @GetMapping("/recommendations")
    public List<EventFullDto> getRecommendations(@RequestHeader("X-EWM-USER-ID") @Positive Long userId) {
        return eventService.getRecommendations(userId);
    }

    @PutMapping("/{eventId}/like")
    public void addLike(@RequestHeader("X-EWM-USER-ID") @Positive Long userId,
                        @PathVariable @Positive Long eventId) {
        eventService.addLike(userId, eventId);
    }
}
