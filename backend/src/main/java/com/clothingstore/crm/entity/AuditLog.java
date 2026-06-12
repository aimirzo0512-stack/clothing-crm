package com.clothingstore.crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable activity / audit trail record. Written by {@code AuditService}
 * whenever a significant action occurs (create/update/delete/login).
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60)
    private String username;

    @Column(nullable = false, length = 60)
    private String action;       // e.g. CREATE, UPDATE, DELETE, LOGIN

    @Column(name = "entity_type", length = 60)
    private String entityType;   // e.g. Customer, Product, Order

    @Column(name = "entity_id")
    private Long entityId;

    @Column(length = 512)
    private String details;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
