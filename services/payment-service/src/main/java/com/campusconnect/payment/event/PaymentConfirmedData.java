package com.campusconnect.payment.event;

import java.math.BigDecimal;

/** Datos del evento PaymentConfirmed (ver docs/02-contratos-eventos.md). */
public record PaymentConfirmedData(
        String paymentId,
        String studentId,
        BigDecimal amount,
        String currency,
        String concept,
        String method,
        String confirmedAt
) {}
