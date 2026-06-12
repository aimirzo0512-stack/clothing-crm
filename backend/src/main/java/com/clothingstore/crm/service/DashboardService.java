package com.clothingstore.crm.service;

import com.clothingstore.crm.dto.DashboardStatsDto;
import com.clothingstore.crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AuditLogRepository auditLogRepository;

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Transactional(readOnly = true)
    public DashboardStatsDto getStats() {
        long totalCustomers = customerRepository.count();
        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.count();
        long lowStock = productRepository.findLowStock().size();

        LocalDate now = LocalDate.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonth = monthStart.plusMonths(1);
        BigDecimal monthlyRevenue = orderRepository.sumRevenueBetween(monthStart, nextMonth);

        List<DashboardStatsDto.ActivityDto> activities = auditLogRepository
                .findTop10ByOrderByCreatedAtDesc().stream()
                .map(a -> new DashboardStatsDto.ActivityDto(
                        a.getUsername(), a.getAction(), a.getEntityType(),
                        a.getDetails(), a.getCreatedAt().format(TS)))
                .toList();

        // Build 14-day sales chart
        LocalDateTime from = now.minusDays(13).atStartOfDay();
        var rows = orderRepository.dailySalesSince(from);
        List<String> labels = new ArrayList<>();
        List<BigDecimal> revenue = new ArrayList<>();
        List<Long> orders = new ArrayList<>();
        for (Object[] row : rows) {
            labels.add(row[0].toString());
            orders.add(((Number) row[1]).longValue());
            revenue.add((BigDecimal) row[2]);
        }
        var chart = new DashboardStatsDto.SalesChart(labels, revenue, orders);

        return new DashboardStatsDto(totalCustomers, totalOrders, totalProducts,
                monthlyRevenue, lowStock, activities, chart);
    }
}
