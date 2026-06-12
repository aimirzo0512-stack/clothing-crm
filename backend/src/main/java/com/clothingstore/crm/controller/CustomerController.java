package com.clothingstore.crm.controller;

import com.clothingstore.crm.dto.ApiResponse;
import com.clothingstore.crm.dto.CustomerDto;
import com.clothingstore.crm.dto.order.OrderDto;
import com.clothingstore.crm.entity.CustomerStatus;
import com.clothingstore.crm.service.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customers")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ApiResponse<Page<CustomerDto>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ApiResponse.ok(customerService.search(search, status, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerDto> get(@PathVariable Long id) {
        return ApiResponse.ok(customerService.getById(id));
    }

    @GetMapping("/{id}/orders")
    public ApiResponse<Page<OrderDto>> purchaseHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(customerService.purchaseHistory(id, PageRequest.of(page, size)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','EMPLOYEE')")
    @PostMapping
    public ApiResponse<CustomerDto> create(@Valid @RequestBody CustomerDto dto) {
        return ApiResponse.ok("Customer created", customerService.create(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER','EMPLOYEE')")
    @PutMapping("/{id}")
    public ApiResponse<CustomerDto> update(@PathVariable Long id, @Valid @RequestBody CustomerDto dto) {
        return ApiResponse.ok("Customer updated", customerService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ApiResponse.ok("Customer deleted", null);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        byte[] body = customerService.exportCsv().getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }
}
