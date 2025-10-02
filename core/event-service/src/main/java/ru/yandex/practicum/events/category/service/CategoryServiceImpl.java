package ru.yandex.practicum.events.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.events.category.mapper.MapperCategory;
import ru.yandex.practicum.events.category.model.Category;
import ru.yandex.practicum.interaction.dto.category.CategoryDto;
import ru.yandex.practicum.interaction.dto.category.NewCategoryDto;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.DuplicateException;
import ru.yandex.practicum.interaction.exception.NotFoundException;
import ru.yandex.practicum.events.category.repository.CategoryRepository;
import ru.yandex.practicum.events.event.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final MapperCategory mapperCategory;
    private final EventRepository eventRepository;

    private Category getCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Категория с ID '%d' не найдена или недоступна", catId)));
    }

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        String catName = newCategoryDto.getName();
        if (categoryRepository.existsByNameIgnoreCase(catName)) {
            throw new DuplicateException(String.format("Категория с именем '%s' уже существует.", catName));
        }
        Category catSave = mapperCategory.toCategory(newCategoryDto);
        return mapperCategory.toCategoryDto(categoryRepository.save(catSave));
    }

    @Transactional
    @Override
    public CategoryDto updateById(Long catId, CategoryDto categoryDto) {
        Category category = getCategory(catId);
        String catName = categoryDto.getName();
        if (!category.getName().equals(catName) && categoryRepository.existsByNameIgnoreCase(catName)) {
            throw new DuplicateException(String.format("Категория с именем '%s' уже существует.", catName));
        } else {
            category.setName(catName);
            return mapperCategory.toCategoryDto(category);
        }
    }

    @Transactional
    @Override
    public void deleteById(Long catId) {
        getCategory(catId);
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("Существуют события, связанные с категорией");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto getById(Long catId) {
        Category category = getCategory(catId);
        return mapperCategory.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return mapperCategory.toCategoryDtoList(categoryRepository.findAll(pageable).toList());
    }
}
