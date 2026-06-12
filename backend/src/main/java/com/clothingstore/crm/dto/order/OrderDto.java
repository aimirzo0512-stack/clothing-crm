package com.clothingstore.crm.dto.order;

import com.clothingstore.crm.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        String orderNumber,
        Long customerId,
        String customerName,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemDto> items,
        LocalDateTime createdAt
) {}
