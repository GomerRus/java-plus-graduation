package ru.yandex.practicum.interaction.feign.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.interaction.dto.event.EventFullDto;

public interface EventInternalApi {

    @GetMapping("/{eventId}")
    ResponseEntity<EventFullDto> getEventById(@PathVariable Long eventId);
}
