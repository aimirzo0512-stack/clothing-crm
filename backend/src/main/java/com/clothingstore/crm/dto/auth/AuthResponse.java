package com.clothingstore.crm.dto.auth;

import com.clothingstore.crm.entity.Role;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String username,
        String fullName,
        Role role,
        long expiresInMs
) {}
