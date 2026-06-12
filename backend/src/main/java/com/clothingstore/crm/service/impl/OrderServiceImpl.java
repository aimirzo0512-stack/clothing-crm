package com.clothingstore.crm.service.impl;

import com.clothingstore.crm.dto.order.CreateOrderRequest;
import com.clothingstore.crm.dto.order.OrderDto;
import com.clothingstore.crm.entity.*;
import com.clothingstore.crm.exception.BadRequestException;
import com.clothingstore.crm.exception.ResourceNotFoundException;
import com.clothingstore.crm.repository.CustomerRepository;
import com.clothingstore.crm.repository.OrderRepository;
import com.clothingstore.crm.repository.ProductRepository;
import com.clothingstore.crm.service.AuditService;
import com.clothingstore.crm.service.OrderService;
import com.clothingstore.crm.util.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> search(OrderStatus status, Pageable pageable) {
        return orderRepository.search(status, pageable).map(OrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getById(Long id) {
        return OrderMapper.toDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public OrderDto create(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.customerId()));

        Order order = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customer(customer)
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.Line line : request.items()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", line.productId()));
            if (product.getStockQuantity() < line.quantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
            // Decrement inventory as part of order creation
            product.setStockQuantity(product.getStockQuantity() - line.quantity());
            productRepository.save(product);

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(line.quantity()));
            total = total.add(lineTotal);

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(line.quantity())
                    .unitPrice(product.getPrice())
                    .lineTotal(lineTotal)
                    .build();
            order.addItem(item);
        }
        order.setTotalAmount(total);
        order = orderRepository.save(order);

        // Update customer aggregates + loyalty points (1 point per currency unit / 10)
        customer.setTotalPurchases(customer.getTotalPurchases().add(total));
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + total.intValue() / 10);
        customerRepository.save(customer);

        auditService.log("CREATE", "Order", order.getId(), "Created order " + order.getOrderNumber());
        return OrderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto updateStatus(Long id, OrderStatus status) {
        Order order = findOrThrow(id);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot change status of a cancelled order");
        }
        order.setStatus(status);
        order = orderRepository.save(order);
        auditService.log("UPDATE", "Order", id, "Order " + order.getOrderNumber() + " -> " + status);
        return OrderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto cancel(Long id) {
        Order order = findOrThrow(id);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Delivered orders cannot be cancelled");
        }
        // Restock products
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        // Reverse customer aggregates
        Customer customer = order.getCustomer();
        customer.setTotalPurchases(customer.getTotalPurchases().subtract(order.getTotalAmount()).max(BigDecimal.ZERO));
        customerRepository.save(customer);

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        auditService.log("CANCEL", "Order", id, "Cancelled order " + order.getOrderNumber());
        return OrderMapper.toDto(order);
    }

    private Order findOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }
}
