package com.clothingstore.crm.controller;

import com.clothingstore.crm.dto.ApiResponse;
import com.clothingstore.crm.dto.ReportDto;
import com.clothingstore.crm.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reports")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER')")
public class ReportController {

    private final ReportService reportService;

    /** period = daily | weekly | monthly */
    @GetMapping("/sales")
    public ApiResponse<ReportDto.SalesReport> sales(@RequestParam(defaultValue = "monthly") String period) {
        return ApiResponse.ok(reportService.salesReport(period));
    }

    @GetMapping("/top-products")
    public ApiResponse<List<ReportDto.TopProduct>> topProducts(@RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.ok(reportService.topSellingProducts(limit));
    }

    @GetMapping("/customer-analytics")
    public ApiResponse<ReportDto.CustomerAnalytics> customerAnalytics() {
        return ApiResponse.ok(reportService.customerAnalytics());
    }
}
