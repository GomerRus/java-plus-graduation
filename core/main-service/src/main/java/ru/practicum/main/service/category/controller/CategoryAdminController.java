package ru.practicum.main.service.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.service.category.dto.CategoryDto;
import ru.practicum.main.service.category.dto.NewCategoryDto;
import ru.practicum.main.service.category.service.CategoryService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Post/admin/categories: create- {}, - Start", newCategoryDto);
        CategoryDto categoryDto = categoryService.create(newCategoryDto);
        log.info("Post/admin/categories: create- {}, - Finish", categoryDto);
        return categoryDto;
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable Long catId,
                              @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Patch/admin/categories/catId: update- {}, {} - Start", catId, categoryDto);
        CategoryDto updatedCategoryDto = categoryService.updateById(catId, categoryDto);
        log.info("Patch/admin/categories/catId: update- {}, - Finish", updatedCategoryDto);
        return updatedCategoryDto;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long catId) {
        log.info("Delete/admin/categories/catId: deleteById- {}, - Start", catId);
        categoryService.deleteById(catId);
    }
}