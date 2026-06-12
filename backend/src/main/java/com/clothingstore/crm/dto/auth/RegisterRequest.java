package com.clothingstore.crm.dto.auth;

import com.clothingstore.crm.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 60) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password,
        @Size(max = 120) String fullName,
        Role role // optional; defaults to EMPLOYEE when null
) {}
