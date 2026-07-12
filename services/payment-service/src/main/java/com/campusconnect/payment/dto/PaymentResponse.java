package com.campusconnect.payment.dto;

import com.campusconnect.payment.domain.Payment;

import java.math.BigDecimal;

public record PaymentResponse(
        String paymentId,
        String studentId,
        String concept,
        BigDecimal amount,
        String currency,
        String method,
        String status,
        String createdAt,
        String confirmedAt
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getCode(), p.getStudentId(), p.getConcept(), p.getAmount(), p.getCurrency(),
                p.getMethod(), p.getStatus().name(), p.getCreatedAt().toString(),
                p.getConfirmedAt() == null ? null : p.getConfirmedAt().toString());
    }
}
