package ru.yandex.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.yandex.practicum.interaction.dto.event.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.interaction.request.ConfirmedRequestsDto;
import ru.yandex.practicum.interaction.request.ParticipationRequestDto;
import ru.yandex.practicum.interaction.enums.request.RequestStatus;
import ru.yandex.practicum.request.model.ConfirmedRequests;
import ru.yandex.practicum.request.model.Request;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperRequest {
    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "requesterId", target = "requester")
    ParticipationRequestDto toParticipationRequestDto(Request request);

    @Named("stateFromEventRequestStatusUpdateRequest")
    default RequestStatus statusFromUpdateRequestStatus(EventRequestStatusUpdateRequest.Status status) {
        if (status == EventRequestStatusUpdateRequest.Status.REJECTED) {
            return RequestStatus.REJECTED;
        } else {
            return RequestStatus.CONFIRMED;
        }
    }

    ConfirmedRequestsDto toConfirmedRequestsDto(ConfirmedRequests confirmedRequests);
}
