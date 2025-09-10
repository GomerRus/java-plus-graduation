package ru.practicum.main.service.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.service.event.dto.EventFullDto;
import ru.practicum.main.service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.main.service.event.dto.EventShortDto;
import ru.practicum.main.service.event.dto.NewEventDto;
import ru.practicum.main.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.main.service.event.dto.UpdateEventUserRequest;
import ru.practicum.main.service.event.service.param.GetEventAdminParam;
import ru.practicum.main.service.event.service.param.GetEventUserParam;
import ru.practicum.main.service.request.dto.ParticipationRequestDto;

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
}
