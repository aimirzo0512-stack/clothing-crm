package com.clothingstore.crm.entity;

/**
 * Application roles used for role-based access control (RBAC).
 * Stored on the {@link User} entity and mapped to Spring Security authorities
 * prefixed with "ROLE_".
 */
public enum Role {
    ADMIN,
    SALES_MANAGER,
    EMPLOYEE
}
