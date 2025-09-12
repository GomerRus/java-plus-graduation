package ru.practicum.main.service.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.main.service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main.service.request.dto.ParticipationRequestDto;
import ru.practicum.main.service.request.enums.RequestStatus;
import ru.practicum.main.service.request.model.Request;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperRequest {
    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDto toParticipationRequestDto(Request request);

    @Named("stateFromEventRequestStatusUpdateRequest")
    default RequestStatus statusFromUpdateRequestStatus(EventRequestStatusUpdateRequest.Status status) {
        if (status == EventRequestStatusUpdateRequest.Status.REJECTED) {
            return RequestStatus.REJECTED;
        } else {
            return RequestStatus.CONFIRMED;
        }
    }
}
