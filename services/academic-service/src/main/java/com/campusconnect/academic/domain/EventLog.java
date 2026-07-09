package com.campusconnect.academic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Registro local de los eventos publicados por este servicio. Alimenta el endpoint
 * de historial (GET /academic/students/{id}/events) y sirve como evidencia de trazabilidad.
 */
@Entity
@Table(name = "event_log")
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;
    private String eventType;
    private String correlationId;
    private Long studentId;

    @Column(columnDefinition = "text")
    private String payload;

    private Instant occurredAt;

    protected EventLog() {
        // requerido por JPA
    }

    public EventLog(String eventId, String eventType, String correlationId,
                    Long studentId, String payload, Instant occurredAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.correlationId = correlationId;
        this.studentId = studentId;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public String getCorrelationId() { return correlationId; }
    public Long getStudentId() { return studentId; }
    public String getPayload() { return payload; }
    public Instant getOccurredAt() { return occurredAt; }
}
