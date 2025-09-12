package ru.practicum.main.service.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.category.dto.CategoryDto;
import ru.practicum.main.service.category.dto.NewCategoryDto;
import ru.practicum.main.service.category.mapper.MapperCategory;
import ru.practicum.main.service.category.model.Category;
import ru.practicum.main.service.category.repository.CategoryRepository;
import ru.practicum.main.service.event.EventRepository;
import ru.practicum.main.service.exception.ConflictException;
import ru.practicum.main.service.exception.DuplicateException;
import ru.practicum.main.service.exception.NotFoundException;

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
