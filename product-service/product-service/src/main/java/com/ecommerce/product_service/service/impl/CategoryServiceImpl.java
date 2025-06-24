package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.Dto.category.CategoryResponseDto;
import com.ecommerce.product_service.Dto.category.CreateCategoryDto;
import com.ecommerce.product_service.exception.CategoryNotFoundException;
import com.ecommerce.product_service.Entity.Category;
import com.ecommerce.product_service.Entity.Product;
import com.ecommerce.product_service.repository.CategoryRepository;
import com.ecommerce.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto createCategory(CreateCategoryDto createCategoryDto) {
        Category category = new Category();
        category.setName(createCategoryDto.getName());
        Category savedCategory =  categoryRepository.save(category);
        return new CategoryResponseDto(savedCategory.getId(), savedCategory.getName());
    }

    @Override
    public void deleteCategory(Integer id) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new CategoryNotFoundException("There is no category already for id = " + id));
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponseDto> categoryResponse = new ArrayList<>();
        for (Category category : categories) {
            CategoryResponseDto categoryResponseDto = new CategoryResponseDto(
                    category.getId(),
                    category.getName());
            categoryResponse.add(categoryResponseDto);
        }
        return categoryResponse;
    }

}
