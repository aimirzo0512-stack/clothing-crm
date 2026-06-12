package com.clothingstore.crm.repository;

import com.clothingstore.crm.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop10ByOrderByCreatedAtDesc();
    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
