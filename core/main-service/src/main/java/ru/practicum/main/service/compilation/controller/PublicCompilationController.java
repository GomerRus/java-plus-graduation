package ru.practicum.main.service.compilation.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.service.compilation.dto.CompilationDto;
import ru.practicum.main.service.compilation.service.CompilationService;
import ru.practicum.main.service.compilation.service.GetCompilationsParam;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                @Min(0) @RequestParam(defaultValue = "0") Integer from,
                                                @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET /compilations с параметрами pinned = {}, from = {}, size = {}", pinned, from, size);
        Pageable page = PageRequest.of(from, size);
        GetCompilationsParam param = GetCompilationsParam.builder()
                .pinned(pinned)
                .pageable(page)
                .build();

        List<CompilationDto> compilations = compilationService.getCompilations(param);
        log.info("Успешно получены compilations с параметрами pinned = {}, from = {}, size = {}. Найдено: {}",
                pinned,
                from,
                size,
                compilations.size());
        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@Min(1) @PathVariable Long compId) {
        log.info("Получен GET /compilations {}", compId);
        CompilationDto compilation = compilationService.getCompilationById(compId);
        log.info("Найден compilation с eventId = {}", compId);
        return compilation;
    }
}
