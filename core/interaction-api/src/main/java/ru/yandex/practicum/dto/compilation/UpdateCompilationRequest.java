package ru.yandex.practicum.dto.compilation;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.validator.SizeAfterTrim;

import java.util.Set;

@Getter
@Setter
public class UpdateCompilationRequest {
    @SizeAfterTrim(min = 1, max = 50)
    private String title;

    private Boolean pinned;

    private Set<Long> events;

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
