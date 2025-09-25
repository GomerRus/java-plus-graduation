package ru.yandex.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.event.EventFullDto;
import ru.yandex.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.yandex.practicum.dto.event.EventShortDto;
import ru.yandex.practicum.dto.event.NewEventDto;
import ru.yandex.practicum.dto.event.UpdateEventAdminRequest;
import ru.yandex.practicum.dto.event.UpdateEventUserRequest;
import ru.yandex.practicum.dto.request.ParticipationRequestDto;
import ru.yandex.practicum.event.service.param.GetEventAdminParam;
import ru.yandex.practicum.event.service.param.GetEventUserParam;

import java.util.List;

public interface EventService {

    List<EventFullDto> getEventsByAdmin(GetEventAdminParam param);

    List<EventShortDto> getEventsByUser(GetEventUserParam param);

    EventFullDto getEventForUser(Long userId, Long eventId);

    List<EventShortDto> getAllUsersEvents(Long userId, Pageable page);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventDto);

    EventFullDto addNewEvent(Long userId, NewEventDto eventDto);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventDto);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);

    EventFullDto getEventById(Long eventId);

    EventFullDto getEventByIdAnyState(Long eventId);
}
