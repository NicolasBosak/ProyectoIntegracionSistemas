package com.campusconnect.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegisterDebtRequest(
        @NotBlank String studentId,
        @NotBlank String concept,
        @NotNull BigDecimal amount
) {}
