package com.campusconnect.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Mensaje que terminó en la Dead Letter Queue (fallo tras reintentos). Queda registrado
 * como "mensaje fallido" y puede reprocesarse manualmente.
 */
@Entity
@Table(name = "dead_letters")
public class DeadLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;
    private String eventType;
    private String studentId;
    private String correlationId;
    private String reason;

    @Column(columnDefinition = "text")
    private String payload;           // JSON del evento original (para reprocesar)

    private String status;            // FAILED | REPROCESSED
    private Instant createdAt = Instant.now();

    protected DeadLetter() {
    }

    public DeadLetter(String eventId, String eventType, String studentId, String correlationId,
                      String reason, String payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.studentId = studentId;
        this.correlationId = correlationId;
        this.reason = reason;
        this.payload = payload;
        this.status = "FAILED";
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public String getStudentId() { return studentId; }
    public String getCorrelationId() { return correlationId; }
    public String getReason() { return reason; }
    public String getPayload() { return payload; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
}
