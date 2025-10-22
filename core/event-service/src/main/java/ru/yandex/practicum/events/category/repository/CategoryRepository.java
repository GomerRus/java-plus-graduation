package ru.yandex.practicum.events.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.events.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Boolean existsByNameIgnoreCase(String name);
}
