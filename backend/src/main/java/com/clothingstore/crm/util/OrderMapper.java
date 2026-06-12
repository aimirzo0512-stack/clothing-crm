package com.clothingstore.crm.util;

import com.clothingstore.crm.dto.order.OrderDto;
import com.clothingstore.crm.dto.order.OrderItemDto;
import com.clothingstore.crm.entity.Order;

import java.util.List;

/** Small helper to map Order entities to their DTO representation. */
public final class OrderMapper {

    private OrderMapper() {}

    public static OrderDto toDto(Order o) {
        List<OrderItemDto> items = o.getItems().stream()
                .map(i -> new OrderItemDto(
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getLineTotal()))
                .toList();
        return new OrderDto(
                o.getId(),
                o.getOrderNumber(),
                o.getCustomer().getId(),
                o.getCustomer().getFullName(),
                o.getStatus(),
                o.getTotalAmount(),
                items,
                o.getCreatedAt());
    }
}
