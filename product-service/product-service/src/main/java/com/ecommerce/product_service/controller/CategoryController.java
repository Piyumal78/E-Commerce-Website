package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.Dto.category.CategoryResponseDto;
import com.ecommerce.product_service.Dto.category.CreateCategoryDto;
import com.ecommerce.product_service.exception.CategoryNotFoundException;
import com.ecommerce.product_service.Entity.Category;
import com.ecommerce.product_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CreateCategoryDto createCategoryDto) {
        CategoryResponseDto categoryResponseDto = categoryService.createCategory(createCategoryDto);
        URI location = URI.create("/api/categories/" + categoryResponseDto.getId());
        return ResponseEntity.created(location).body(categoryResponseDto);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer id) throws CategoryNotFoundException {
        categoryService.deleteCategory(id);
    }

    @GetMapping("categories")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categoryResponseDtoList = categoryService.getAllCategories();
        return ResponseEntity.ok(categoryResponseDtoList);
    }
}
