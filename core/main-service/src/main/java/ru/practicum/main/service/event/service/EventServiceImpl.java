package ru.practicum.main.service.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.Constants;
import ru.practicum.main.service.category.model.Category;
import ru.practicum.main.service.category.repository.CategoryRepository;
import ru.practicum.main.service.event.EventRepository;
import ru.practicum.main.service.event.LocationRepository;
import ru.practicum.main.service.event.MapperEvent;
import ru.practicum.main.service.event.dto.EventFullDto;
import ru.practicum.main.service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.service.event.dto.EventShortDto;
import ru.practicum.main.service.event.dto.NewEventDto;
import ru.practicum.main.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.service.event.dto.UpdateEventParam;
import ru.practicum.main.service.event.dto.UpdateEventUserRequest;
import ru.practicum.main.service.event.enums.EventState;
import ru.practicum.main.service.event.model.Event;
import ru.practicum.main.service.event.model.QEvent;
import ru.practicum.main.service.event.service.param.GetEventAdminParam;
import ru.practicum.main.service.event.service.param.GetEventUserParam;
import ru.practicum.main.service.event.util.ResponseEventBuilder;
import ru.practicum.main.service.exception.BadRequestException;
import ru.practicum.main.service.exception.ConflictException;
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
import java.util.Objects;

import static ru.practicum.main.service.Constants.CATEGORY_NOT_FOUND;
import static ru.practicum.main.service.Constants.EVENT_NOT_FOUND;
import static ru.practicum.main.service.event.dto.UpdateEventUserRequest.StateAction.SEND_TO_REVIEW;
import static ru.practicum.main.service.event.enums.EventState.CANCELED;
import static ru.practicum.main.service.event.enums.EventState.PENDING;
import static ru.practicum.main.service.event.util.ValidatorEventTime.isEventTimeBad;
import static ru.practicum.main.service.event.dto.UpdateEventAdminRequest.StateAction.PUBLISH_EVENT;
import static ru.practicum.main.service.event.enums.EventState.PUBLISHED;
import static ru.practicum.main.service.event.enums.EventState.REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final MapperEvent eventMapper;
    private final RequestRepository requestRepository;
    private final MapperRequest requestMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ResponseEventBuilder responseEventBuilder;

    @Override
    public List<EventFullDto> getEventsByAdmin(GetEventAdminParam param) {
        QEvent event = QEvent.event;
        BooleanBuilder requestBuilder = new BooleanBuilder();
        if (param.hasUsers()) {
            requestBuilder.and(event.initiator.id.in(param.getUsers()));
        }

        if (param.hasStates()) {
            requestBuilder.and(event.state.in(param.getStates()));
        }

        if (param.hasCategories()) {
            requestBuilder.and(event.category.id.in(param.getCategories()));
        }

        if (param.hasRangeStart()) {
            requestBuilder.and(event.createdOn.gt(param.getRangeStart()));
        }

        if (param.hasRangeEnd()) {
            requestBuilder.and(event.createdOn.lt(param.getRangeEnd()));
        }

        List<Event> events = eventRepository.findAll(requestBuilder, param.getPage()).getContent();
        return responseEventBuilder.buildManyEventResponseDto(events, EventFullDto.class);
    }

    @Override
    public List<EventShortDto> getEventsByUser(GetEventUserParam param) {
        QEvent event = QEvent.event;

        BooleanBuilder requestBuilder = new BooleanBuilder();

        requestBuilder.and(event.state.eq(PUBLISHED));

        if (param.hasText()) {
            BooleanExpression descriptionExpression = event.description.like(param.getText());
            BooleanExpression annotationExpression = event.annotation.like(param.getText());
            requestBuilder.andAnyOf(descriptionExpression, annotationExpression);
        }

        if (param.hasCategories()) {
            requestBuilder.and(event.category.id.in(param.getCategories()));
        }

        if (param.hasPaid()) {
            requestBuilder.and(event.paid.eq(param.getPaid()));
        }

        requestBuilder.and(event.eventDate.gt(Objects.requireNonNullElseGet(param.getRangeStart(), LocalDateTime::now)));

        if (param.hasRangeEnd()) {
            requestBuilder.and(event.eventDate.lt(param.getRangeEnd()));
        }

        List<Event> events = eventRepository.findAll(requestBuilder, param.getPage()).getContent();
        List<EventShortDto> eventDtos = responseEventBuilder.buildManyEventResponseDto(events, EventShortDto.class);

        if (param.getOnlyAvailable()) {
            eventDtos.removeIf(dto -> dto.getConfirmedRequests() == dto.getParticipantLimit());
        }

        return eventDtos;
    }

    @Override
    public List<EventShortDto> getAllUsersEvents(Long userId, Pageable page) {
        List<Event> events = eventRepository.findByInitiatorId(userId, page);
        return responseEventBuilder.buildManyEventResponseDto(events, EventShortDto.class);
    }

    @Override
    @Transactional
    public EventFullDto addNewEvent(Long userId, NewEventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);

        if (isEventTimeBad(eventDto.getEventDate(), 2)) {
            throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }

        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(
                () -> new NotFoundException(CATEGORY_NOT_FOUND));
        event.setCategory(category);

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        event.setInitiator(initiator);

        event.getLocation().setEvent(event);
        locationRepository.save(event.getLocation());

        event = eventRepository.save(event);
        return responseEventBuilder.buildOneEventResponseDto(event, EventFullDto.class);
    }

    @Override
    public EventFullDto getEventForUser(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND));
        return responseEventBuilder.buildOneEventResponseDto(event, EventFullDto.class);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateDto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND));

        EventState state = event.getState();
        if (state == PUBLISHED) {
            throw new ConflictException("Изменить можно только не опубликованные события, текущий статус " + state);
        }

        if (updateDto.hasStateAction()) {
            if (updateDto.getStateAction().equals(SEND_TO_REVIEW)) {
                event.setState(PENDING);
            } else {
                event.setState(CANCELED);
            }
        }

        if (updateDto.hasEventDate()) {
            if (isEventTimeBad(updateDto.getEventDate(), 2)) {
                throw new BadRequestException("Дата начала изменяемого события должна быть не ранее чем за 2 часа от даты публикации");
            }
            event.setEventDate(updateDto.getEventDate());
        }

        UpdateEventParam param = eventMapper.toUpdateParam(updateDto);
        updateEvent(event, param);

        return responseEventBuilder.buildOneEventResponseDto(event, EventFullDto.class);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND));

        if (isPreModerationOff(event.getRequestModeration(), event.getParticipantLimit())) {
            return result;
        }

        List<Request> requestsAll = requestRepository.findAllByEventId(eventId);
        List<Request> requestsStatusPending = requestsAll.stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .filter(r -> updateRequest.getRequestIds().contains(r.getId()))
                .toList();

        if (requestsStatusPending.size() != updateRequest.getRequestIds().size()) {
            throw new ConflictException("Один или более запросов не находится в статусе PENDING");
        }

        if (updateRequest.getStatus().equals(EventRequestStatusUpdateRequest.Status.REJECTED)) {
            for (Request request : requestsStatusPending) {
                request.setStatus(RequestStatus.REJECTED);
                ParticipationRequestDto dto = requestMapper.toParticipationRequestDto(request);
                result.getRejectedRequests().add(dto);
            }

            return result;
        }

        long participantCount = requestsAll.stream()
                .filter(r -> r.getStatus() == RequestStatus.CONFIRMED)
                .count();

        if (participantCount == event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит заявок на событие");
        }

        long limitLeft = event.getParticipantLimit() - participantCount;

        int idx = 0;
        while (idx < requestsStatusPending.size() && limitLeft > 0) {
            Request request = requestsStatusPending.get(idx);
            request.setStatus(RequestStatus.CONFIRMED);

            ParticipationRequestDto dto = requestMapper.toParticipationRequestDto(request);
            result.getConfirmedRequests().add(dto);

            limitLeft--;
            idx++;
        }

        while (idx < requestsStatusPending.size()) {
            Request request = requestsStatusPending.get(idx);
            request.setStatus(RequestStatus.CANCELED);

            ParticipationRequestDto dto = requestMapper.toParticipationRequestDto(request);
            result.getRejectedRequests().add(dto);

            idx++;
        }

        return result;
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
        Event eventDomain = eventRepository.findByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new NotFoundException(Constants.EVENT_NOT_FOUND));
        return responseEventBuilder.buildOneEventResponseDto(eventDomain, EventFullDto.class);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Constants.EVENT_NOT_FOUND));

        if (event.getState() != EventState.PENDING) {
            throw new ConflictException("Изменить можно только события ожидающие модерацию, текущий статус " + event.getState());
        }

        if (updateDto.hasStateAction()) {
            EventState state;

            if (updateDto.getStateAction() == PUBLISH_EVENT) {
                state = PUBLISHED;
                event.setPublishedOn(LocalDateTime.now());
            } else {
                state = REJECTED;
            }

            event.setState(state);
        }

        if (updateDto.hasEventDate()) {
            if (isEventTimeBad(updateDto.getEventDate(), 1)) {
                throw new BadRequestException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
            }
            event.setEventDate(updateDto.getEventDate());
        }

        UpdateEventParam param = eventMapper.toUpdateParam(updateDto);
        updateEvent(event, param);

        return responseEventBuilder.buildOneEventResponseDto(event, EventFullDto.class);
    }

    private void updateEvent(Event event, UpdateEventParam param) {
        if (param.hasCategory()) {
            Category category = categoryRepository.findById(param.getCategory())
                    .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND));
            event.setCategory(category);
        }

        if (param.hasAnnotation()) {
            event.setAnnotation(param.getAnnotation());
        }

        if (param.hasDescription()) {
            event.setDescription(param.getDescription());
        }

        if (param.hasLocation()) {
            event.getLocation().setLatitude(param.getLocation().getLatitude());
            event.getLocation().setLongitude(param.getLocation().getLongitude());
        }

        if (param.hasPaid()) {
            event.setPaid(param.getPaid());
        }

        if (param.hasParticipantLimit()) {
            event.setParticipantLimit(param.getParticipantLimit());
        }

        if (param.hasRequestModeration()) {
            event.setRequestModeration(param.getRequestModeration());
        }

        if (param.hasTitle()) {
            event.setTitle(param.getTitle());
        }
    }

    private boolean isPreModerationOff(boolean moderationStatus, int limit) {
        return !moderationStatus || limit == 0;
    }
}
