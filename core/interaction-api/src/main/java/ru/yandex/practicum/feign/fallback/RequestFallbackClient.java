package ru.yandex.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.request.ConfirmedRequestsDto;
import ru.yandex.practicum.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.enums.request.RequestStatus;
import ru.yandex.practicum.exception.ServiceUnavailableException;
import ru.yandex.practicum.feign.api.RequestInternalApi;

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
}
