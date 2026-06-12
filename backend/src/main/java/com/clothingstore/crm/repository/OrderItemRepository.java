package com.clothingstore.crm.repository;

import com.clothingstore.crm.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // [productId, productName, totalQty, totalRevenue] for top-selling products
    @Query("""
            SELECT oi.product.id, oi.product.name, SUM(oi.quantity), SUM(oi.lineTotal)
            FROM OrderItem oi
            WHERE oi.order.status <> com.clothingstore.crm.entity.OrderStatus.CANCELLED
            GROUP BY oi.product.id, oi.product.name
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<Object[]> topSellingProducts(org.springframework.data.domain.Pageable pageable);
}
