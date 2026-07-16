package com.campusconnect.analytics.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/** Registro de cada evento procesado (trazabilidad para GET /analytics/events). */
@Entity
@Table(name = "event_records")
public class EventRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;
    private String eventType;
    private String correlationId;
    private String source;
    private String occurredAt;
    private Instant receivedAt = Instant.now();

    protected EventRecord() {
    }

    public EventRecord(String eventId, String eventType, String correlationId,
                       String source, String occurredAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.correlationId = correlationId;
        this.source = source;
        this.occurredAt = occurredAt;
        this.receivedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public String getCorrelationId() { return correlationId; }
    public String getSource() { return source; }
    public String getOccurredAt() { return occurredAt; }
    public Instant getReceivedAt() { return receivedAt; }
}
