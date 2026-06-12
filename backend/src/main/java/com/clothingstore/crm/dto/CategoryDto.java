package com.clothingstore.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDto(
        Long id,
        @NotBlank @Size(max = 80) String name,
        @Size(max = 255) String description
) {}
