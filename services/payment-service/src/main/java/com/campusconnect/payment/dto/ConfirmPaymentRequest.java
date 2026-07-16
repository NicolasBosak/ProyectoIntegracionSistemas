package com.campusconnect.payment.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ConfirmPaymentRequest(
        @NotBlank String studentId,
        BigDecimal amount,
        String concept,
        String method
) {}
