package com.clothingstore.crm.dto;

import com.clothingstore.crm.entity.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Used for both request (create/update) and response. Read-only fields
 * (id, totalPurchases, loyaltyPoints, registrationDate) are ignored on input.
 */
public record CustomerDto(
        Long id,
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email String email,
        @Size(max = 30) String phoneNumber,
        @Size(max = 255) String address,
        LocalDate registrationDate,
        BigDecimal totalPurchases,
        Integer loyaltyPoints,
        CustomerStatus status
) {}
