package com.clothingstore.crm.controller;

import com.clothingstore.crm.dto.ApiResponse;
import com.clothingstore.crm.dto.DashboardStatsDto;
import com.clothingstore.crm.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsDto> stats() {
        return ApiResponse.ok(dashboardService.getStats());
    }
}
