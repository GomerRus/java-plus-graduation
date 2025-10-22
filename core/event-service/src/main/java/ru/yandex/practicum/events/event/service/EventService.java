package ru.yandex.practicum.events.event.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.interaction.dto.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.interaction.dto.event.EventRequestStatusUpdateResult;
import ru.yandex.practicum.interaction.dto.event.EventShortDto;
import ru.yandex.practicum.interaction.dto.event.NewEventDto;
import ru.yandex.practicum.interaction.dto.event.UpdateEventAdminRequest;
import ru.yandex.practicum.interaction.dto.event.UpdateEventUserRequest;
import ru.yandex.practicum.interaction.request.ParticipationRequestDto;
import ru.yandex.practicum.events.event.service.param.GetEventAdminParam;
import ru.yandex.practicum.events.event.service.param.GetEventUserParam;

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

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto getEventByIdAnyState(Long eventId);

    List<EventFullDto> getRecommendations(Long userId);

    void addLike(Long userId, Long eventId);
}
