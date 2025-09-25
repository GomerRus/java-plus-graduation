package ru.practicum.stats.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.error.BadRequestException;
import ru.practicum.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statService;

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                       @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                       @RequestParam(name = "uris", required = false) List<String> uris,
                                                       @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        if (end.isBefore(start)) {
            throw new BadRequestException("end < start");
        }

        log.info("Пришел запрос на сервер статистики GET /stats");
        List<ViewStatsDto> stats = statService.getStats(start, end, uris, unique);
        log.info("Статистика собрана. GET /stats отработал без ошибок, size = {}", stats.size());
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @PostMapping("/hit")
    public ResponseEntity<String> hitStat(@Valid @RequestBody EndpointHitDto hitDto) {
        log.info("Пришел запрос на сервис статистики POST /hit");
        statService.saveHit(hitDto);
        log.info("Информация сохранена. POST /hit отработал без ошибок");
        return new ResponseEntity<>("Информация сохранена", HttpStatus.CREATED);
    }
}