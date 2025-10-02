package ru.yandex.practicum.events.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.events.category.model.Category;
import ru.yandex.practicum.interaction.dto.category.CategoryDto;
import ru.yandex.practicum.interaction.dto.category.NewCategoryDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperCategory {
    Category toCategory(CategoryDto categoryDto);

    Category toCategory(NewCategoryDto newCategoryDto);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDtoList(List<Category> categoryList);
}

