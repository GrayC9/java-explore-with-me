package ru.practicum.explorewithme.category.service;

import ru.practicum.explorewithme.category.dto.CategoryDto;
import ru.practicum.explorewithme.category.dto.NewCategoryDto;
import ru.practicum.explorewithme.category.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void removeCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    List<CategoryDto> findCategories(Integer from, Integer size);

    CategoryDto findCategoryById(Long categoryId);

    Category findCategory(Long categoryId);
}
