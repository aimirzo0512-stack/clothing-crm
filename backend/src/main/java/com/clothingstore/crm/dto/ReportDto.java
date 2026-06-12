package com.clothingstore.crm.dto;

import java.math.BigDecimal;
import java.util.List;

public class ReportDto {

    public record SalesReport(
            String period,        // DAILY / WEEKLY / MONTHLY
            String from,
            String to,
            long orderCount,
            BigDecimal totalRevenue,
            List<Bucket> buckets
    ) {}

    public record Bucket(String label, long orders, BigDecimal revenue) {}

    public record TopProduct(Long productId, String name, long unitsSold, BigDecimal revenue) {}

    public record CustomerAnalytics(
            long totalCustomers,
            long activeCustomers,
            long vipCustomers,
            BigDecimal averageLifetimeValue,
            List<TopCustomer> topCustomers
    ) {}

    public record TopCustomer(Long id, String fullName, BigDecimal totalPurchases, Integer loyaltyPoints) {}
}
