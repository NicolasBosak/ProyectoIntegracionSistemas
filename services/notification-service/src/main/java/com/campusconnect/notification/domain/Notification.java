package com.campusconnect.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;              // NOT-001

    private String studentId;
    private String channel;           // SIMULATED_EMAIL
    private String triggerEvent;      // StudentEnrolled, PaymentConfirmed, ...
    private String correlationId;

    @Column(columnDefinition = "text")
    private String message;

    private String status;            // SENT | FAILED
    private String reason;            // motivo si status = FAILED
    private Instant createdAt = Instant.now();

    protected Notification() {
    }

    public Notification(String studentId, String channel, String triggerEvent,
                        String correlationId, String message, String status, String reason) {
        this.studentId = studentId;
        this.channel = channel;
        this.triggerEvent = triggerEvent;
        this.correlationId = correlationId;
        this.message = message;
        this.status = status;
        this.reason = reason;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStudentId() { return studentId; }
    public String getChannel() { return channel; }
    public String getTriggerEvent() { return triggerEvent; }
    public String getCorrelationId() { return correlationId; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Instant getCreatedAt() { return createdAt; }
}
