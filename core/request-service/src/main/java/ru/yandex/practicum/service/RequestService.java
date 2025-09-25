package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.request.ConfirmedRequestsDto;
import ru.yandex.practicum.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.enums.request.RequestStatus;

import java.util.Collection;
import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getParticipationRequests(Long userId);

    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByEventId(Long eventId);

    int getRequestsCountByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ConfirmedRequestsDto> getConfirmedRequestsByEventId(Collection<Long> eventIds);

    ParticipationRequestDto changeRequestStatus(Long requestId, RequestStatus status);
}
