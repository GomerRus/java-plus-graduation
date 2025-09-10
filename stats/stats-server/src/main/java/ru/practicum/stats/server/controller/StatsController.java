package ru.practicum.stats.server.controller;

import client.StatParam;
import client.StatsClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.error.BadRequestException;
import ru.practicum.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hitStat(@Valid @RequestBody EndpointHitDto hitDto) {
        log.info("Пришел запрос на сервис статистики POST /hit");
        statService.saveHit(hitDto);
        log.info("Информация сохранена. POST /hit отработал без ошибок");
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStat(@RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(name = "uris", required = false) List<String> uris,
                                       @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        if (end.isBefore(start)) {
            throw new BadRequestException("end < start");
        }

        log.info("Пришел запрос на сервер статистики GET /stats");
        List<ViewStatsDto> stats = statService.getStats(start, end, uris, unique);
        log.info("Статистика собрана. GET /stats отработал без ошибок, size = {}", stats.size());
        return stats;
    }
}