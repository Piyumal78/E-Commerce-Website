package com.ecommerce.product_service.Dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateProductDto {
    @NotBlank(message = "Name is required!")
    private String name;

    @NotNull(message = "Quantity is required!")
    @Positive(message = "Invalid amount!")
    private Integer quantity;

    @NotNull(message = "Price is required!")
    @Positive(message = "Invalid price!")
    private Float price;

    @NotNull(message = "Category Id is required!")
    private Integer categoryId;
}
