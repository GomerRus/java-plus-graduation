package ru.yandex.practicum.interaction.feign.api;

import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.request.ConfirmedRequestsDto;
import ru.yandex.practicum.interaction.request.ParticipationRequestDto;
import ru.yandex.practicum.interaction.enums.request.RequestStatus;

import java.util.Collection;
import java.util.List;

public interface RequestInternalApi {
    @GetMapping("/event/{eventId}")
    List<ParticipationRequestDto> getRequestsByEventId(@PathVariable Long eventId);

    @GetMapping("/event/{eventId}/count")
    int getRequestsCountByEventIdAndStatus(@PathVariable Long eventId, @RequestParam(name = "status") RequestStatus status);

    @GetMapping("/confirmed")
    List<ConfirmedRequestsDto> getConfirmedRequestsByEventId(@RequestParam(name = "ids") Collection<Long> eventsIds);

    @PutMapping("/{requestId}/confirm")
    ParticipationRequestDto updateRequestStatus(@PathVariable Long requestId, @RequestBody RequestStatus status);

    @GetMapping("/users/{userId}/event/{eventId}")
    ParticipationRequestDto getUserEventRequest(@PathVariable(name = "userId") Long userId, @PathVariable(name = "eventId") Long eventId);
}

