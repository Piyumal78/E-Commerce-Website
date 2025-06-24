package com.ecommerce.product_service.Dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponseDto {
    private Integer id;
    private String productmName;
    private Integer quantity;
    private Float unitPrice;
    private String categoryName;
}
