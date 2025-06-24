package com.ecommerce.productservice.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {


    @Id
    @GeneratedValue
    private UUID id;
    @NotBlank(message = "Category name is required")
    private String name;
    
    private String description;
    
    private Boolean active;
    

}
