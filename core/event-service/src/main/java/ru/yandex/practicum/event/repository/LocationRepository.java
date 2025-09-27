package ru.yandex.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
