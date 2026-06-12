package com.clothingstore.crm.controller;

import com.clothingstore.crm.dto.ApiResponse;
import com.clothingstore.crm.dto.ProductDto;
import com.clothingstore.crm.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "Inventory")
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final ProductService productService;

    @GetMapping("/low-stock")
    public ApiResponse<List<ProductDto>> lowStock() {
        return ApiResponse.ok(productService.lowStock());
    }

    /** Simple inventory report: total SKUs, total stock value, low-stock count. */
    @GetMapping("/report")
    public ApiResponse<Map<String, Object>> report() {
        var low = productService.lowStock();
        return ApiResponse.ok(Map.of(
                "lowStockCount", low.size(),
                "lowStockItems", low
        ));
    }
}
