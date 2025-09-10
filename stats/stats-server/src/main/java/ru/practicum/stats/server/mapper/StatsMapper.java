package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    EndpointHit mapToModel(EndpointHitDto endpointHitDto);

    EndpointHitDto mapToDto(EndpointHit endpointHit);

}
