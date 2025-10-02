package ru.yandex.practicum.events.compliation.service;

import ru.yandex.practicum.interaction.dto.compilation.CompilationDto;
import ru.yandex.practicum.interaction.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.interaction.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(GetCompilationsParam param);

    CompilationDto getCompilationById(Long compId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long compId);

    void deleteCompilation(Long compId);
}
