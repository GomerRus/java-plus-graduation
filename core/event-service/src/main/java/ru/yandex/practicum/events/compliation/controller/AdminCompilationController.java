package ru.yandex.practicum.events.compliation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.dto.compilation.CompilationDto;
import ru.yandex.practicum.interaction.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.interaction.dto.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.events.compliation.service.CompilationService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Получен POST /admin/compilations title = {}", newCompilationDto.getTitle());
        CompilationDto compilation = compilationService.createCompilation(newCompilationDto);
        log.info("Успешно создана подборка title = {}, eventId = {}", compilation.getTitle(), compilation.getId());
        return compilation;
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@Min(1) @PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Получен PATCH /admin/compilations/{}", compId);
        CompilationDto compilation = compilationService.updateCompilation(updateCompilationRequest, compId);
        log.info("Успешно обновлена подборка eventId = {}", compilation.getId());
        return compilation;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@Min(1) @PathVariable Long compId) {
        log.info("Получен DELETE /admin/compilations/{}", compId);
        compilationService.deleteCompilation(compId);
        log.info("Подборка успешно удалена, eventId = {}", compId);
    }
}
