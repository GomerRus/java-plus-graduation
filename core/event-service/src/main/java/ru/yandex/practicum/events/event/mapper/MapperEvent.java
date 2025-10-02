package ru.yandex.practicum.events.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.yandex.practicum.interaction.dto.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.EventShortDto;
import ru.yandex.practicum.interaction.dto.event.NewEventDto;
import ru.yandex.practicum.interaction.dto.event.UpdateEventAdminRequest;
import ru.yandex.practicum.interaction.dto.event.UpdateEventParam;
import ru.yandex.practicum.interaction.dto.event.UpdateEventUserRequest;
import ru.yandex.practicum.interaction.enums.event.EventState;
import ru.yandex.practicum.events.event.model.Event;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperEvent {
    @Mapping(source = "category", target = "category.id", ignore = true)
    Event toEvent(NewEventDto newEventDto);

    @Mapping(source = "category", target = "category.id", ignore = true)
    @Mapping(source = "stateAction", target = "state", qualifiedByName = "stateFromAdminAction")
    Event toEvent(UpdateEventAdminRequest updateEventAdminRequest);

    @Mapping(source = "category", target = "category.id", ignore = true)
    @Mapping(source = "stateAction", target = "state", qualifiedByName = "stateFromUserAction")
    Event toEvent(UpdateEventUserRequest updateEventUserRequest);

    EventShortDto toEventShortDto(Event event);

    EventFullDto toEventFullDto(Event event);

    UpdateEventParam toUpdateParam(UpdateEventAdminRequest request);

    UpdateEventParam toUpdateParam(UpdateEventUserRequest request);

    @Named("stateFromAdminAction")
    default EventState stateFromAdminAction(UpdateEventAdminRequest.StateAction action) {
        if (action == null) {
            return null;
        }

        if (action == UpdateEventAdminRequest.StateAction.PUBLISH_EVENT) {
            return EventState.PUBLISHED;
        } else {
            return EventState.REJECTED;
        }
    }

    @Named("stateFromUserAction")
    default EventState stateFromUserAction(UpdateEventUserRequest.StateAction action) {
        if (action == null) {
            return null;
        }

        if (action == UpdateEventUserRequest.StateAction.SEND_TO_REVIEW) {
            return EventState.PENDING;
        } else {
            return EventState.CANCELED;
        }
    }
}
