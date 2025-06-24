package com.ecommerce.product_service.service;

import com.ecommerce.product_service.Dto.product.CreateProductDto;
import com.ecommerce.product_service.Dto.product.ChangeProductAmountDto;
import com.ecommerce.product_service.Dto.product.ProductResponseDto;
import com.ecommerce.product_service.Dto.product.UpdateProductDto;
import com.ecommerce.product_service.exception.CategoryNotFoundException;
import com.ecommerce.product_service.exception.InsufficientAmountException;
import com.ecommerce.product_service.exception.ProductNotFoundException;

import java.util.List;

public interface ProductService {
    ProductResponseDto CreateNewProduct(CreateProductDto createProductDto) throws CategoryNotFoundException;
    void deleteProductById(Integer id) throws ProductNotFoundException;
    ProductResponseDto getProductById(Integer id) throws ProductNotFoundException;
    //List<ProductResponseDto> getAllProducts();
    ProductResponseDto updateProductById(Integer id, UpdateProductDto updateProductDto)
            throws ProductNotFoundException, CategoryNotFoundException;
    List<ProductResponseDto> getProductsByCategoryId(Integer categoryId) throws CategoryNotFoundException;
    void changeProductAmount(ChangeProductAmountDto changeProductAmountDto) throws ProductNotFoundException, InsufficientAmountException;
}
