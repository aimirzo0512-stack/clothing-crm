package com.clothingstore.crm.controller;

import com.clothingstore.crm.dto.ApiResponse;
import com.clothingstore.crm.dto.order.CreateOrderRequest;
import com.clothingstore.crm.dto.order.OrderDto;
import com.clothingstore.crm.dto.order.UpdateStatusRequest;
import com.clothingstore.crm.entity.OrderStatus;
import com.clothingstore.crm.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<Page<OrderDto>> list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ApiResponse.ok(orderService.search(status, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDto> get(@PathVariable Long id) {
        return ApiResponse.ok(orderService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','EMPLOYEE')")
    @PostMapping
    public ApiResponse<OrderDto> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok("Order created", orderService.create(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER')")
    @PatchMapping("/{id}/status")
    public ApiResponse<OrderDto> updateStatus(@PathVariable Long id,
                                              @Valid @RequestBody UpdateStatusRequest request) {
        return ApiResponse.ok("Order status updated", orderService.updateStatus(id, request.status()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER')")
    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderDto> cancel(@PathVariable Long id) {
        return ApiResponse.ok("Order cancelled", orderService.cancel(id));
    }
}
