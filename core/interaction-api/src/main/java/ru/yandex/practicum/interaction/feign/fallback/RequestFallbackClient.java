package ru.yandex.practicum.interaction.feign.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.request.ConfirmedRequestsDto;
import ru.yandex.practicum.interaction.request.ParticipationRequestDto;
import ru.yandex.practicum.interaction.enums.request.RequestStatus;
import ru.yandex.practicum.interaction.exception.ServiceUnavailableException;
import ru.yandex.practicum.interaction.feign.api.RequestInternalApi;

import java.util.Collection;
import java.util.List;

@Component
public class RequestFallbackClient implements RequestInternalApi {

    private static final String SERVICE_NAME = "request-service";

    @Override
    public List<ParticipationRequestDto> getRequestsByEventId(Long eventId) {
        throw new ServiceUnavailableException(SERVICE_NAME);
    }

    @Override
    public int getRequestsCountByEventIdAndStatus(Long eventId, RequestStatus status) {
        throw new ServiceUnavailableException(SERVICE_NAME);
    }

    @Override
    public List<ConfirmedRequestsDto> getConfirmedRequestsByEventId(Collection<Long> eventsIds) {
        throw new ServiceUnavailableException(SERVICE_NAME);
    }

    @Override
    public ParticipationRequestDto updateRequestStatus(Long requestId, RequestStatus status) {
        throw new ServiceUnavailableException(SERVICE_NAME);
    }

    @Override
    public ParticipationRequestDto getUserEventRequest(Long userId, Long eventId) {
        throw new ServiceUnavailableException(SERVICE_NAME);
    }
}
