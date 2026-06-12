package com.clothingstore.crm.dto.order;

import java.math.BigDecimal;

public record OrderItemDto(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
