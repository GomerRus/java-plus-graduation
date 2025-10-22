package ru.yandex.practicum.events.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.events.category.model.Category;
import ru.yandex.practicum.client.RecommendationClientGrpc;
import ru.yandex.practicum.client.UserActionClientGrpc;
import ru.yandex.practicum.events.event.model.QEvent;
import ru.yandex.practicum.events.event.mapper.MapperEvent;
import ru.yandex.practicum.events.event.model.Event;
import ru.yandex.practicum.events.event.model.Location;
import ru.yandex.practicum.interaction.feign.client.RequestFeignClient;
import ru.yandex.practicum.interaction.feign.client.UserFeignClient;
import ru.yandex.practicum.events.category.repository.CategoryRepository;
import ru.yandex.practicum.events.event.repository.EventRepository;
import ru.yandex.practicum.events.event.repository.LocationRepository;
import ru.yandex.practicum.events.event.service.param.GetEventAdminParam;
import ru.yandex.practicum.events.event.service.param.GetEventUserParam;
import ru.yandex.practicum.events.event.util.ResponseEventBuilder;
import ru.yandex.practicum.events.event.util.ValidatorEventTime;
import ru.yandex.practicum.interaction.utility.Constants;
import ru.yandex.practicum.interaction.dto.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.interaction.dto.event.EventRequestStatusUpdateResult;
import ru.yandex.practicum.interaction.dto.event.EventShortDto;
import ru.yandex.practicum.interaction.dto.event.NewEventDto;
import ru.yandex.practicum.interaction.dto.event.UpdateEventAdminRequest;
import ru.yandex.practicum.interaction.dto.event.UpdateEventParam;
import ru.yandex.practicum.interaction.dto.event.UpdateEventUserRequest;
import ru.yandex.practicum.interaction.enums.event.EventState;
import ru.yandex.practicum.interaction.exception.BadRequestException;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NotFoundException;
import ru.yandex.practicum.interaction.request.ParticipationRequestDto;
import ru.yandex.practicum.interaction.enums.request.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.yandex.practicum.events.event.util.ValidatorEventTime.isEventTimeBad;
import static ru.yandex.practicum.interaction.utility.Constants.CATEGORY_NOT_FOUND;
import static ru.yandex.practicum.interaction.utility.Constants.EVENT_NOT_FOUND;
import static ru.yandex.practicum.interaction.dto.event.UpdateEventUserRequest.StateAction.SEND_TO_REVIEW;
import static ru.yandex.practicum.interaction.enums.event.EventState.CANCELED;
import static ru.yandex.practicum.interaction.enums.event.EventState.PENDING;
import static ru.yandex.practicum.interaction.dto.event.UpdateEventAdminRequest.StateAction.PUBLISH_EVENT;
import static ru.yandex.practicum.interaction.enums.event.EventState.PUBLISHED;
import static ru.yandex.practicum.interaction.enums.event.EventState.REJECTED;
import static ru.yandex.practicum.interaction.utility.Constants.MAX_RECOMMENDATION_COUNT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final MapperEvent eventMapper;
    private final RequestFeignClient requestFeignClient;
    private final UserFeignClient userFeignClient;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ResponseEventBuilder responseEventBuilder;
    private final UserActionClientGrpc userActionClientGrpc;
    private final RecommendationClientGrpc recommendationClientGrpc;

    @Override
    public List<EventFullDto> getEventsByAdmin(GetEventAdminParam param) {
        QEvent event = QEvent.event;
        BooleanBuilder requestBuilder = new BooleanBuilder();
        if (param.hasUsers()) {
            requestBuilder.and(event.initiatorId.in(param.getUsers()));
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
        Location location = new Location();
        location.setLatitude(eventDto.getLocation().getLatitude());
        location.setLongitude(eventDto.getLocation().getLongitude());
        event.setLocation(location);

        if (ValidatorEventTime.isEventTimeBad(eventDto.getEventDate(), 2)) {
            throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }

        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(
                () -> new NotFoundException(CATEGORY_NOT_FOUND));
        event.setCategory(category);

        userFeignClient.getUserById(userId);
        event.setInitiatorId(userId);

        event.getLocation().setEvent(event);
        locationRepository.save(event.getLocation());

        event = eventRepository.save(event);
        return responseEventBuilder.buildOneEventResponseDto(event, EventFullDto.class);
    }

    @Override
    public EventFullDto getEventForUser(Long userId, Long eventId) {
        userFeignClient.getUserById(userId);
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
        userFeignClient.getUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND));
        if (!event.getInitiatorId().equals(userId)) {
            throw new ConflictException("Пользователь не может смотреть заявки на мероприятие, если он не является его инициатором");
        }
        return requestFeignClient.getRequestsByEventId(eventId);
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

        List<ParticipationRequestDto> requestsAll = requestFeignClient.getRequestsByEventId(eventId);
        List<ParticipationRequestDto> requestsStatusPending = requestsAll.stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .filter(r -> updateRequest.getRequestIds().contains(r.getId()))
                .toList();

        if (requestsStatusPending.size() != updateRequest.getRequestIds().size()) {
            throw new ConflictException("Один или более запросов не находится в статусе PENDING");
        }

        if (updateRequest.getStatus().equals(EventRequestStatusUpdateRequest.Status.REJECTED)) {
            for (ParticipationRequestDto request : requestsStatusPending) {
                request.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(request);
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
            ParticipationRequestDto request = requestsStatusPending.get(idx);
            request.setStatus(RequestStatus.CONFIRMED);

            result.getConfirmedRequests().add(request);
            requestFeignClient.updateRequestStatus(request.getId(), RequestStatus.CONFIRMED);
            limitLeft--;
            idx++;
        }

        while (idx < requestsStatusPending.size()) {
            ParticipationRequestDto request = requestsStatusPending.get(idx);
            request.setStatus(RequestStatus.REJECTED);
            requestFeignClient.updateRequestStatus(request.getId(), RequestStatus.REJECTED);
            result.getRejectedRequests().add(request);

            idx++;
        }

        return result;
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        Event eventDomain = eventRepository.findByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new NotFoundException(Constants.EVENT_NOT_FOUND));

        userActionClientGrpc.handleUserEventView(userId, eventId);

        return responseEventBuilder.buildOneEventResponseDto(eventDomain, EventFullDto.class);
    }

    @Override
    public EventFullDto getEventByIdAnyState(Long eventId) {
        Event eventDomain = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND));
        return responseEventBuilder.buildOneEventResponseDto(eventDomain, EventFullDto.class);
    }

    @Override
    public List<EventFullDto> getRecommendations(Long userId) {
        List<Event> events = recommendationClientGrpc.getRecommendationsForUser(userId, MAX_RECOMMENDATION_COUNT)
                .map(proto -> proto.getEventId())
                .map(eventRepository::findById)
                .map(Optional::get)
                .toList();

        return responseEventBuilder.buildManyEventResponseDto(events, EventFullDto.class);
    }

    @Override
    public void addLike(Long userId, Long eventId) {
        userFeignClient.getUserById(userId);

        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND));

        ParticipationRequestDto userRequest = requestFeignClient.getUserEventRequest(userId, eventId);
        if (userRequest.getStatus() != RequestStatus.CONFIRMED) {
            throw new BadRequestException("Пользователь должен посетить мероприятие, чтобы поставить ему лайк");
        }
        userActionClientGrpc.handleUserEventLike(userId, eventId);
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
