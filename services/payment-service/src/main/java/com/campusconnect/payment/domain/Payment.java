package com.campusconnect.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/** Obligacion de pago que puede estar PENDING (deuda) o CONFIRMED (pago realizado). */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;              // PAY-001

    private String studentId;
    private String concept;           // Matricula, Pension, ...
    private BigDecimal amount;
    private String currency;
    private String method;            // TRANSFER, CASH, ... (al confirmar)

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    private String correlationId;
    private Instant createdAt = Instant.now();
    private Instant confirmedAt;

    protected Payment() {
    }

    public Payment(String studentId, String concept, BigDecimal amount, String currency,
                   PaymentStatus status, String correlationId) {
        this.studentId = studentId;
        this.concept = concept;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.correlationId = correlationId;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStudentId() { return studentId; }
    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(Instant confirmedAt) { this.confirmedAt = confirmedAt; }
}
