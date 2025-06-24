package com.ecommerce.product_service.Dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeProductAmountDto {
    @NotNull(message = "Product Id is required!")
    private Integer productId;

    @NotNull(message = "Quantity is required!")
    private Integer quantity;
}
