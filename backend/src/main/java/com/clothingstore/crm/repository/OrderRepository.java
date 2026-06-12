package com.clothingstore.crm.repository;

import com.clothingstore.crm.entity.Order;
import com.clothingstore.crm.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    @Query("""
            SELECT o FROM Order o
            WHERE (:status IS NULL OR o.status = :status)
            """)
    Page<Order> search(@Param("status") OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o
            WHERE o.status <> com.clothingstore.crm.entity.OrderStatus.CANCELLED
              AND o.createdAt >= :from AND o.createdAt < :to
            """)
    BigDecimal sumRevenueBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
            SELECT COUNT(o) FROM Order o
            WHERE o.createdAt >= :from AND o.createdAt < :to
            """)
    long countBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // [date, orderCount, revenue] grouped by calendar day for the sales chart
    @Query("""
            SELECT CAST(o.createdAt AS date) AS day, COUNT(o), COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.status <> com.clothingstore.crm.entity.OrderStatus.CANCELLED
              AND o.createdAt >= :from
            GROUP BY CAST(o.createdAt AS date)
            ORDER BY CAST(o.createdAt AS date)
            """)
    List<Object[]> dailySalesSince(@Param("from") LocalDateTime from);
}
