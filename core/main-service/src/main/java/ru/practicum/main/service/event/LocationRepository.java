package ru.practicum.main.service.event;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.service.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
