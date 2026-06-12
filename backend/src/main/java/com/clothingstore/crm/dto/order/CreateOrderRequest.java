package com.clothingstore.crm.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateOrderRequest(
        @NotNull Long customerId,
        @NotEmpty @Valid List<Line> items
) {
    public record Line(
            @NotNull Long productId,
            @NotNull @Min(1) Integer quantity
    ) {}
}
