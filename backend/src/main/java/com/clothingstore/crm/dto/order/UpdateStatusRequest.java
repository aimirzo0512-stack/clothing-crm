package com.clothingstore.crm.dto.order;

import com.clothingstore.crm.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull OrderStatus status) {}
