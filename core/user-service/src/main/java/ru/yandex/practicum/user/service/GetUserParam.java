package ru.yandex.practicum.user.service;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Builder
@Getter
public class GetUserParam {
    private List<Long> ids;
    private Pageable pageable;
}
