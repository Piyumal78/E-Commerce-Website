package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.exception.ResourceNotFoundException;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Creating new product: {}", productDto.getName());
        
        Product product = mapToEntity(productDto);
        
        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndActiveTrue(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));
            product.setCategory(category);
        }
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        
        return mapToDto(savedProduct);
    }
    
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        return mapToDto(product);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        log.info("Fetching all active products");
        
        Page<Product> products = productRepository.findByActiveTrue(pageable);
        return products.map(this::mapToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsWithFilters(String name, Long categoryId, 
                                                 BigDecimal minPrice, BigDecimal maxPrice, 
                                                 Pageable pageable) {
        log.info("Fetching products with filters - name: {}, categoryId: {}, minPrice: {}, maxPrice: {}", 
                name, categoryId, minPrice, maxPrice);
        
        Page<Product> products = productRepository.findProductsWithFilters(name, categoryId, minPrice, maxPrice, pageable);
        return products.map(this::mapToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        log.info("Fetching products for category id: {}", categoryId);
        
        // Verify category exists
        categoryRepository.findByIdAndActiveTrue(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        Page<Product> products = productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
        return products.map(this::mapToDto);
    }
    
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        log.info("Updating product with id: {}", id);
        
        Product existingProduct = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // Update fields
        existingProduct.setName(productDto.getName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setImageUrl(productDto.getImageUrl());
        existingProduct.setStockQuantity(productDto.getStockQuantity());
        
        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndActiveTrue(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));
            existingProduct.setCategory(category);
        }
        
        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully with id: {}", updatedProduct.getId());
        
        return mapToDto(updatedProduct);
    }
    
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // Soft delete
        product.setActive(false);
        productRepository.save(product);
        
        log.info("Product deleted successfully with id: {}", id);
    }
    
    public ProductDto updateStock(Long id, Integer quantity) {
        log.info("Updating stock for product id: {} to quantity: {}", id, quantity);
        
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        product.setStockQuantity(quantity);
        Product updatedProduct = productRepository.save(product);
        
        log.info("Stock updated successfully for product id: {}", id);
        return mapToDto(updatedProduct);
    }
    
    @Transactional(readOnly = true)
    public List<ProductDto> getLowStockProducts(Integer threshold) {
        log.info("Fetching products with stock below threshold: {}", threshold);
        
        List<Product> products = productRepository.findLowStockProducts(threshold);
        return products.stream().map(this::mapToDto).toList();
    }
    
    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setActive(product.getActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        
        return dto;
    }
    
    private Product mapToEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setStockQuantity(dto.getStockQuantity());
        product.setActive(dto.getActive() != null ? dto.getActive() : true);
        
        return product;
    }
}
