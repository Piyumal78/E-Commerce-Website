package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.Dto.product.CreateProductDto;
import com.ecommerce.product_service.Dto.product.ChangeProductAmountDto;
import com.ecommerce.product_service.Dto.product.ProductResponseDto;
import com.ecommerce.product_service.Dto.product.UpdateProductDto;
import com.ecommerce.product_service.exception.CategoryNotFoundException;
import com.ecommerce.product_service.exception.InsufficientAmountException;
import com.ecommerce.product_service.exception.ProductNotFoundException;
import com.ecommerce.product_service.Entity.Category;
import com.ecommerce.product_service.Entity.Product;
import com.ecommerce.product_service.repository.CategoryRepository;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponseDto CreateNewProduct(CreateProductDto createProductDto) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(createProductDto.getCategoryId()).orElseThrow(() ->
                new CategoryNotFoundException("There is no category for id = " + createProductDto.getCategoryId()));

        Product product = new Product();
        product.setProductName(createProductDto.getName());
        product.setQuantity(createProductDto.getQuantity());
        product.setUnitPrice(createProductDto.getPrice());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return new ProductResponseDto(
                savedProduct.getId(),
                savedProduct.getProductName(),
                savedProduct.getQuantity(),
                savedProduct.getUnitPrice(),
                savedProduct.getCategory().getName()
        );
    }

    @Override
    public void deleteProductById(Integer id) throws ProductNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException("There is no product with id = " + id));
        productRepository.delete(product);
    }

    @Override
    public ProductResponseDto getProductById(Integer id) throws ProductNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException("There is no product with id = " + id));

        return new ProductResponseDto(
                product.getId(),
                product.getProductName(),
                product.getQuantity(),
                product.getUnitPrice(),
                product.getCategory().getName()
        );
    }

    @Override
    public ProductResponseDto updateProductById(Integer id, UpdateProductDto updateProductDto)
            throws ProductNotFoundException, CategoryNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException("There is no product with id = " + id));

        if (updateProductDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateProductDto.getCategoryId()).orElseThrow(() ->
                    new CategoryNotFoundException("There is no category with id = " + updateProductDto.getCategoryId()));
            product.setCategory(category);
        }

        if (updateProductDto.getName() != null) {
            product.setProductName(updateProductDto.getName());
        }

        if (updateProductDto.getQuantity() != null) {
            product.setQuantity(updateProductDto.getQuantity());
        }

        if (updateProductDto.getPrice() != null) {
            product.setUnitPrice(updateProductDto.getPrice());
        }

        Product savedProduct = productRepository.save(product);

        return new ProductResponseDto(
                savedProduct.getId(),
                savedProduct.getProductName(),
                savedProduct.getQuantity(),
                savedProduct.getUnitPrice(),
                savedProduct.getCategory().getName()
        );
    }

    @Override
    public List<ProductResponseDto> getProductsByCategoryId(Integer categoryId) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new CategoryNotFoundException("There is no category with id = " + categoryId));

        List<Product> products = category.getProducts();
        List<ProductResponseDto> productResponses = new ArrayList<>();

        for (Product product : products) {
            productResponses.add(new ProductResponseDto(
                    product.getId(),
                    product.getProductName(),
                    product.getQuantity(),
                    product.getUnitPrice(),
                    product.getCategory().getName()
            ));
        }

        return productResponses;
    }

    @Override
    @Transactional(rollbackFor = {ProductNotFoundException.class, InsufficientAmountException.class})
    public void changeProductAmount(ChangeProductAmountDto changeProductAmountDto)
            throws ProductNotFoundException, InsufficientAmountException {

        Product product = productRepository.findById(changeProductAmountDto.getProductId()).orElseThrow(() ->
                new ProductNotFoundException("There is no product with id = " + changeProductAmountDto.getProductId()));

        Integer balance = product.getQuantity() + changeProductAmountDto.getQuantity();

        if (balance < 0) {
            throw new InsufficientAmountException("Only " + product.getQuantity() + " " + product.getProductName() + " are available");
        }

        product.setQuantity(balance);
        productRepository.save(product);
    }
}
