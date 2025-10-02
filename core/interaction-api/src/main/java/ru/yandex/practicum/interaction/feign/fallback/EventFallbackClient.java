package ru.yandex.practicum.interaction.feign.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.event.EventFullDto;
import ru.yandex.practicum.interaction.exception.ServiceUnavailableException;
import ru.yandex.practicum.interaction.feign.api.EventInternalApi;

@Component
public class EventFallbackClient implements EventInternalApi {

    private static final String SERVICE_NAME = "event-service";

    @Override
    public ResponseEntity<EventFullDto> getEventById(Long eventId) {
        throw new ServiceUnavailableException(SERVICE_NAME);
    }
}
