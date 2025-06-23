package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.CategoryDto;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.exception.ResourceNotFoundException;
import com.ecommerce.productservice.exception.DuplicateResourceException;
import com.ecommerce.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.info("Creating new category: {}", categoryDto.getName());
        
        if (categoryRepository.existsByNameAndActiveTrue(categoryDto.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + categoryDto.getName());
        }
        
        Category category = mapToEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        
        log.info("Category created successfully with id: {}", savedCategory.getId());
        return mapToDto(savedCategory);
    }
    
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        log.info("Fetching category with id: {}", id);
        
        Category category = categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        return mapToDto(category);
    }
    
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        log.info("Fetching all active categories");
        
        List<Category> categories = categoryRepository.findByActiveTrue();
        return categories.stream().map(this::mapToDto).toList();
    }
    
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        log.info("Updating category with id: {}", id);
        
        Category existingCategory = categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // Check if name is being changed and if new name already exists
        if (!existingCategory.getName().equals(categoryDto.getName()) && 
            categoryRepository.existsByNameAndActiveTrue(categoryDto.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + categoryDto.getName());
        }
        
        existingCategory.setName(categoryDto.getName());
        existingCategory.setDescription(categoryDto.getDescription());
        
        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Category updated successfully with id: {}", updatedCategory.getId());
        
        return mapToDto(updatedCategory);
    }
    
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        
        Category category = categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        // Soft delete
        category.setActive(false);
        categoryRepository.save(category);
        
        log.info("Category deleted successfully with id: {}", id);
    }
    
    private CategoryDto mapToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setActive(category.getActive());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        
        return dto;
    }
    
    private Category mapToEntity(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setActive(dto.getActive() != null ? dto.getActive() : true);
        
        return category;
    }
}
