package ru.yandex.practicum.compliation.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

@Builder
@Getter
@ToString
public class GetCompilationsParam {
    private final Boolean pinned;
    private final Pageable pageable;
}
