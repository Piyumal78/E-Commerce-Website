package com.ecommerce.product_service.controller;

    import com.ecommerce.product_service.Dto.product.CreateProductDto;
    import com.ecommerce.product_service.Dto.product.ChangeProductAmountDto;
    import com.ecommerce.product_service.Dto.product.ProductResponseDto;
    import com.ecommerce.product_service.Dto.product.UpdateProductDto;
    import com.ecommerce.product_service.exception.CategoryNotFoundException;
    import com.ecommerce.product_service.exception.InsufficientAmountException;
    import com.ecommerce.product_service.exception.ProductNotFoundException;
    import com.ecommerce.product_service.service.ProductService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.bind.annotation.*;

    import java.net.URI;
    import java.util.List;

    @RestController
    @RequestMapping("/api")
    @RequiredArgsConstructor
    public class ProductController {

        private final ProductService productService;

        @PostMapping("/products")
        public ResponseEntity<ProductResponseDto> createNewProduct(
                @Valid @RequestBody CreateProductDto createProductDto) throws CategoryNotFoundException {
            ProductResponseDto productResponseDto = productService.CreateNewProduct(createProductDto);
            URI location = URI.create("/api/products/" + productResponseDto.getId());
            return ResponseEntity.created(location).body(productResponseDto);
        }

        @DeleteMapping("/products/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteProduct(@PathVariable Integer id) throws ProductNotFoundException {
            productService.deleteProductById(id);
        }

        @GetMapping("/products/{id}")
        public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Integer id) throws ProductNotFoundException {
            ProductResponseDto productResponseDto = productService.getProductById(id);
            return ResponseEntity.ok(productResponseDto);
        }

        @PatchMapping("/products/{id}")
        public ResponseEntity<ProductResponseDto> updateProduct(
                @PathVariable Integer id, @Valid @RequestBody UpdateProductDto updateProductDto)
                throws ProductNotFoundException, CategoryNotFoundException {
            ProductResponseDto productResponseDto = productService.updateProductById(id, updateProductDto);
            return ResponseEntity.ok(productResponseDto);
        }

        @GetMapping("/categories/{categoryId}/products")
        public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(
                @PathVariable Integer categoryId) throws CategoryNotFoundException {
            List<ProductResponseDto> productResponseDtoList = productService.getProductsByCategoryId(categoryId);
            return ResponseEntity.ok(productResponseDtoList);
        }

        @PostMapping("/products/amount-changes")
        public void changeProductAmount(
                @RequestBody @Valid ChangeProductAmountDto changeProductAmountDto)
                throws ProductNotFoundException, InsufficientAmountException {
            productService.changeProductAmount(changeProductAmountDto);
        }
    }