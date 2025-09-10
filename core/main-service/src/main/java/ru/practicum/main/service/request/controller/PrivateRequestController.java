package ru.practicum.main.service.request.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.service.request.dto.ParticipationRequestDto;
import ru.practicum.main.service.request.service.RequestService;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getParticipationRequests(@Min(1) @PathVariable Long userId) {
        log.info("Получен GET /users/{}/requests", userId);
        List<ParticipationRequestDto> participationRequests = requestService.getParticipationRequests(userId);
        log.info("Найдено {} заявок от пользователя {}", participationRequests.size(), userId);
        return participationRequests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@Min(1) @PathVariable Long userId,
                                                              @Min(1) @RequestParam Long eventId) {
        log.info("Получен POST /users/{}/requests , eventId = {}", userId, eventId);
        ParticipationRequestDto participationRequest = requestService.createParticipationRequest(userId, eventId);
        log.info("Успешно создана заявка пользователем = {} на участие в мероприятити = {}", userId, eventId);
        return participationRequest;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@Min(1) @PathVariable Long userId,
                                                              @Min(1) @PathVariable Long requestId) {
        log.info("Получен PATCH /users/{}/requests , eventId = {}", userId, requestId);
        ParticipationRequestDto participationRequest = requestService.cancelParticipationRequest(userId, requestId);
        log.info("Успешно отменена заявка пользователем = {} на участие в мероприятити = {}", userId, requestId);
        return participationRequest;
    }
}
