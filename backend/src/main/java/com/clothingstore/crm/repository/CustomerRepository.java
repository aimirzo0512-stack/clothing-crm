package com.clothingstore.crm.repository;

import com.clothingstore.crm.entity.Customer;
import com.clothingstore.crm.entity.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    @Query("""
            SELECT c FROM Customer c
            WHERE (:search IS NULL OR
                   LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   c.phoneNumber LIKE CONCAT('%', :search, '%'))
              AND (:status IS NULL OR c.status = :status)
            """)
    Page<Customer> search(@Param("search") String search,
                          @Param("status") CustomerStatus status,
                          Pageable pageable);

    long countByStatus(CustomerStatus status);
}
