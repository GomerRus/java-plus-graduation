package ru.practicum.main.service.compilation.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.service.validator.SizeAfterTrim;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    @SizeAfterTrim(min = 1, max = 50)
    String title;

    Boolean pinned;

    Set<Long> events;

    public boolean hasTitle() {
        return title != null;
    }

    public boolean hasPinned() {
        return pinned != null;
    }

    public boolean hasEvents() {
        return events != null;
    }
}
