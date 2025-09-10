package ru.practicum.main.service.event.service.param;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import ru.practicum.main.service.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class GetEventAdminParam {
    List<Long> users;
    List<EventState> states;
    List<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Pageable page;

    public boolean hasUsers() {
        return users != null;
    }

    public boolean hasStates() {
        return states != null;
    }

    public boolean hasCategories() {
        return categories != null;
    }

    public boolean hasRangeStart() {
        return rangeStart != null;
    }

    public boolean hasRangeEnd() {
        return rangeEnd != null;
    }
}
