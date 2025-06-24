package com.ecommerce.product_service.Dto.product;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateProductDto {
    private String name;

    @Positive(message = "Invalid amount!")
    private Integer quantity;

    @Positive(message = "Invalid unitprice!")
    private Float price;

    private Integer categoryId;
}
