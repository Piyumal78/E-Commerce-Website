package com.ecommerce.product_service.Dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryDto {
    @NotBlank(message = "Name is required!")
    private String name;
}
