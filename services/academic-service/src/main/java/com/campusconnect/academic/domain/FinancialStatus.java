package com.campusconnect.academic.domain;

/** Estado financiero del estudiante, actualizado por el evento PaymentConfirmed (Paso 5). */
public enum FinancialStatus {
    PENDING,
    UP_TO_DATE,
    OVERDUE
}
