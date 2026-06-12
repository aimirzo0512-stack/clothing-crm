package com.clothingstore.crm.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsDto(
        long totalCustomers,
        long totalOrders,
        long totalProducts,
        BigDecimal monthlyRevenue,
        long lowStockCount,
        List<ActivityDto> recentActivities,
        SalesChart salesChart
) {
    public record ActivityDto(
            String username,
            String action,
            String entityType,
            String details,
            String timestamp
    ) {}

    /** Parallel arrays describing daily sales for the chart on the dashboard. */
    public record SalesChart(
            List<String> labels,
            List<BigDecimal> revenue,
            List<Long> orders
    ) {}
}
