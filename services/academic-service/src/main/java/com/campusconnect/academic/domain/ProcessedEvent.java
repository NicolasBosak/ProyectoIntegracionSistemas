package com.campusconnect.academic.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Registro de eventos ya procesados (Idempotent Receiver). Evita reprocesar un evento
 * critico (p. ej. PaymentConfirmed) si llega duplicado o es reentregado.
 */
@Entity
@Table(name = "processed_events")
public class ProcessedEvent {

    @Id
    private String eventId;
    private String eventType;
    private Instant processedAt = Instant.now();

    protected ProcessedEvent() {
    }

    public ProcessedEvent(String eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = Instant.now();
    }

    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public Instant getProcessedAt() { return processedAt; }
}
