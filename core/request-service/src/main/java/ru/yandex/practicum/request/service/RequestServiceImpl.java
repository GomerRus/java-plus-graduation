package ru.yandex.practicum.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.UserActionClientGrpc;
import ru.yandex.practicum.interaction.dto.event.EventFullDto;
import ru.yandex.practicum.interaction.request.ConfirmedRequestsDto;
import ru.yandex.practicum.interaction.dto.user.UserDto;
import ru.yandex.practicum.interaction.enums.event.EventState;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.DuplicateException;
import ru.yandex.practicum.interaction.exception.NotFoundException;
import ru.yandex.practicum.interaction.request.ParticipationRequestDto;
import ru.yandex.practicum.interaction.enums.request.RequestStatus;
import ru.yandex.practicum.interaction.feign.client.EventFeignClient;
import ru.yandex.practicum.interaction.feign.client.UserFeignClient;
import ru.yandex.practicum.request.mapper.MapperRequest;
import ru.yandex.practicum.request.model.ConfirmedRequests;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.interaction.utility.Constants;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventFeignClient eventFeignClient;
    private final UserFeignClient userFeignClient;
    private final MapperRequest mapperRequest;
    private final UserActionClientGrpc userActionClientGrpc;

    @Override
    public List<ParticipationRequestDto> getParticipationRequests(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(mapperRequest::toParticipationRequestDto).toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {

        EventFullDto event = eventFeignClient.getEventById(eventId).getBody();

        UserDto user = userFeignClient.getUserById(userId);

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new DuplicateException("Запрос на такое событие уже есть");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Невозможно создать запрос на неопубликованное событие");
        }


        if (event.getParticipantLimit() != 0 && requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)
                >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит запросов на событие");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Невозможно создать запрос будучи инициатором события");
        }

        boolean isPreModerationOn = isPreModerationOn(event.getRequestModeration(), event.getParticipantLimit());
        Request request = new Request(
                null,
                userId,
                eventId,
                isPreModerationOn ? RequestStatus.PENDING : RequestStatus.CONFIRMED,
                LocalDateTime.now()
        );

        request = requestRepository.save(request);

        userActionClientGrpc.handleUserEventRegister(userId, eventId);

        return mapperRequest.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос не найден"));

        userFeignClient.getUserById(userId);

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new ConflictException("Нельзя отменить заявку, т.к. ее статус не PENDING");
        }

        request.setStatus(RequestStatus.CANCELED);

        request = requestRepository.save(request);

        return mapperRequest.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEventId(Long eventId) {
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(mapperRequest::toParticipationRequestDto).toList();
    }

    @Override
    public int getRequestsCountByEventIdAndStatus(Long eventId, RequestStatus status) {
        return requestRepository.countByEventIdAndStatus(eventId, status);
    }

    @Override
    public List<ConfirmedRequestsDto> getConfirmedRequestsByEventId(Collection<Long> eventsId) {
        List<ConfirmedRequests> participationRequests =
                requestRepository.getConfirmedRequests(eventsId, RequestStatus.CONFIRMED);

        return participationRequests.stream()
                .map(mapperRequest::toConfirmedRequestsDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto changeRequestStatus(Long requestId, RequestStatus status) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(Constants.REQUEST_NOT_FOUND));
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ConflictException("Чтобы поменять статус заявки, она должна быть в статусе PENDING");
        }
        request.setStatus(status);
        return mapperRequest.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto getUserEventRequest(Long userId, Long eventId) {
        Request request = requestRepository.findByRequesterIdAndEventId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Запрос не найден"));
        return mapperRequest.toParticipationRequestDto(request);
    }

    private boolean isPreModerationOn(boolean moderationStatus, int limit) {
        return moderationStatus && limit != 0;
    }
}
