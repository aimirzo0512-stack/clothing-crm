package com.clothingstore.crm.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        @NotBlank @Size(max = 150) String name,
        Long categoryId,
        String categoryName,
        @Size(max = 20) String size,
        @Size(max = 40) String color,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotNull @Min(0) Integer stockQuantity,
        @Min(0) Integer lowStockThreshold,
        String description,
        String imageUrl
) {}
