package com.clothingstore.crm.service;

import com.clothingstore.crm.dto.order.CreateOrderRequest;
import com.clothingstore.crm.dto.order.OrderDto;
import com.clothingstore.crm.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderDto> search(OrderStatus status, Pageable pageable);
    OrderDto getById(Long id);
    OrderDto create(CreateOrderRequest request);
    OrderDto updateStatus(Long id, OrderStatus status);
    OrderDto cancel(Long id);
}
