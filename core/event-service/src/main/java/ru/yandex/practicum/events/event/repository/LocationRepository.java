package ru.yandex.practicum.events.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.events.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
