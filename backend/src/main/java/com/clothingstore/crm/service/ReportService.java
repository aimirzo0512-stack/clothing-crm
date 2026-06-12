package com.clothingstore.crm.service;

import com.clothingstore.crm.dto.ReportDto;
import com.clothingstore.crm.entity.CustomerStatus;
import com.clothingstore.crm.repository.CustomerRepository;
import com.clothingstore.crm.repository.OrderItemRepository;
import com.clothingstore.crm.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    /** period = DAILY | WEEKLY | MONTHLY. Buckets the last N days accordingly. */
    @Transactional(readOnly = true)
    public ReportDto.SalesReport salesReport(String period) {
        LocalDate today = LocalDate.now();
        LocalDateTime from;
        int days;
        switch (period.toUpperCase()) {
            case "DAILY" -> { days = 1; from = today.atStartOfDay(); }
            case "WEEKLY" -> { days = 7; from = today.minusDays(6).atStartOfDay(); }
            default -> { days = 30; from = today.minusDays(29).atStartOfDay(); }
        }
        LocalDateTime to = today.plusDays(1).atStartOfDay();

        long orderCount = orderRepository.countBetween(from, to);
        BigDecimal totalRevenue = orderRepository.sumRevenueBetween(from, to);

        List<ReportDto.Bucket> buckets = new ArrayList<>();
        var rows = orderRepository.dailySalesSince(from);
        for (Object[] row : rows) {
            buckets.add(new ReportDto.Bucket(
                    row[0].toString(),
                    ((Number) row[1]).longValue(),
                    (BigDecimal) row[2]));
        }
        return new ReportDto.SalesReport(period.toUpperCase(), from.toLocalDate().toString(),
                today.toString(), orderCount, totalRevenue, buckets);
    }

    @Transactional(readOnly = true)
    public List<ReportDto.TopProduct> topSellingProducts(int limit) {
        return orderItemRepository.topSellingProducts(PageRequest.of(0, limit)).stream()
                .map(r -> new ReportDto.TopProduct(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        ((Number) r[2]).longValue(),
                        (BigDecimal) r[3]))
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportDto.CustomerAnalytics customerAnalytics() {
        long total = customerRepository.count();
        long active = customerRepository.countByStatus(CustomerStatus.ACTIVE);
        long vip = customerRepository.countByStatus(CustomerStatus.VIP);

        var top = customerRepository.findAll(PageRequest.of(0, 5,
                org.springframework.data.domain.Sort.by("totalPurchases").descending()));
        BigDecimal sum = BigDecimal.ZERO;
        List<ReportDto.TopCustomer> topCustomers = new ArrayList<>();
        for (var c : top) {
            sum = sum.add(c.getTotalPurchases());
            topCustomers.add(new ReportDto.TopCustomer(c.getId(), c.getFullName(),
                    c.getTotalPurchases(), c.getLoyaltyPoints()));
        }
        BigDecimal avg = total == 0 ? BigDecimal.ZERO
                : sum.divide(BigDecimal.valueOf(Math.min(total, 5)), 2, RoundingMode.HALF_UP);
        return new ReportDto.CustomerAnalytics(total, active, vip, avg, topCustomers);
    }
}
