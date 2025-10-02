package ru.yandex.practicum.interaction.dto.event;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.interaction.request.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();

    private List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
}
