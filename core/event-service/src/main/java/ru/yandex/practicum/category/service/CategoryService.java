package ru.yandex.practicum.category.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.category.CategoryDto;
import ru.yandex.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto updateById(Long catId, CategoryDto categoryDto);

    void deleteById(Long catId);

    CategoryDto getById(Long catId);

    List<CategoryDto> getAll(Pageable pageable);

}
