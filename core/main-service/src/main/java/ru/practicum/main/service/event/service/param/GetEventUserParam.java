package ru.practicum.main.service.event.service.param;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class GetEventUserParam {
    String text;
    List<Long> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Boolean onlyAvailable;
    Pageable page;

    public boolean hasText() {
        return text != null;
    }

    public boolean hasCategories() {
        return categories != null;
    }

    public boolean hasPaid() {
        return paid != null;
    }

    public boolean hasRangeStart() {
        return rangeStart != null;
    }

    public boolean hasRangeEnd() {
        return rangeEnd != null;
    }
}
