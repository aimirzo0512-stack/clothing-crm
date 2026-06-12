package com.clothingstore.crm.controller;

import com.clothingstore.crm.dto.ApiResponse;
import com.clothingstore.crm.dto.ProductDto;
import com.clothingstore.crm.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<Page<ProductDto>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ApiResponse.ok(productService.search(search, categoryId, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDto> get(@PathVariable Long id) {
        return ApiResponse.ok(productService.getById(id));
    }

    @GetMapping("/low-stock")
    public ApiResponse<List<ProductDto>> lowStock() {
        return ApiResponse.ok(productService.lowStock());
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER')")
    @PostMapping
    public ApiResponse<ProductDto> create(@Valid @RequestBody ProductDto dto) {
        return ApiResponse.ok("Product created", productService.create(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER')")
    @PutMapping("/{id}")
    public ApiResponse<ProductDto> update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        return ApiResponse.ok("Product updated", productService.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.ok("Product deleted", null);
    }
}
