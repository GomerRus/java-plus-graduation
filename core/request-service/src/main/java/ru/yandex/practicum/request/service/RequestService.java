package ru.yandex.practicum.request.service;

import ru.yandex.practicum.interaction.request.ConfirmedRequestsDto;
import ru.yandex.practicum.interaction.request.ParticipationRequestDto;
import ru.yandex.practicum.interaction.enums.request.RequestStatus;

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

    ParticipationRequestDto getUserEventRequest(Long userId, Long eventId);
}
