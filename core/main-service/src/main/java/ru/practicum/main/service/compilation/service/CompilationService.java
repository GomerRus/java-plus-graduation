package ru.practicum.main.service.compilation.service;

import ru.practicum.main.service.compilation.dto.CompilationDto;
import ru.practicum.main.service.compilation.dto.NewCompilationDto;
import ru.practicum.main.service.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(GetCompilationsParam param);

    CompilationDto getCompilationById(Long compId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long compId);

    void deleteCompilation(Long compId);
}
