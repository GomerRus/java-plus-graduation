package ru.practicum.main.service.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.service.event.EventRepository;
import ru.practicum.main.service.event.enums.EventState;
import ru.practicum.main.service.event.model.Event;
import ru.practicum.main.service.exception.ConflictException;
import ru.practicum.main.service.exception.DuplicateException;
import ru.practicum.main.service.exception.NotFoundException;
import ru.practicum.main.service.request.MapperRequest;
import ru.practicum.main.service.request.RequestRepository;
import ru.practicum.main.service.request.dto.ParticipationRequestDto;
import ru.practicum.main.service.request.enums.RequestStatus;
import ru.practicum.main.service.request.model.Request;
import ru.practicum.main.service.user.UserRepository;
import ru.practicum.main.service.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final MapperRequest mapperRequest;

    @Override
    public List<ParticipationRequestDto> getParticipationRequests(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(mapperRequest::toParticipationRequestDto).toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие не найдено"));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден!"));

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
                user,
                event,
                isPreModerationOn ? RequestStatus.PENDING : RequestStatus.CONFIRMED,
                LocalDateTime.now()
        );

        request = requestRepository.save(request);

        return mapperRequest.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос не найден"));

        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        request.setStatus(RequestStatus.CANCELED);

        request = requestRepository.save(request);

        return mapperRequest.toParticipationRequestDto(request);
    }

    private boolean isPreModerationOn(boolean moderationStatus, int limit) {
        return moderationStatus && limit != 0;
    }
}
