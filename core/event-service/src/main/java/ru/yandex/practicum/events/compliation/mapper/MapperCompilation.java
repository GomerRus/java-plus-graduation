package ru.yandex.practicum.events.compliation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.events.compliation.model.Compilation;
import ru.yandex.practicum.interaction.dto.compilation.CompilationDto;
import ru.yandex.practicum.interaction.dto.compilation.NewCompilationDto;
import ru.yandex.practicum.interaction.dto.compilation.UpdateCompilationRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperCompilation {
    @Mapping(source = "events", target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto);

    @Mapping(source = "events", target = "events", ignore = true)
    Compilation toCompilation(UpdateCompilationRequest updateCompilationRequest);

    CompilationDto toCompilationDto(Compilation compilation);
}

