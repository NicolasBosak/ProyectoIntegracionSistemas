package com.campusconnect.academic.dto;

import com.campusconnect.academic.domain.EventLog;

/** Entrada del historial de eventos asociados a un estudiante. */
public record EventLogResponse(
        String eventId,
        String eventType,
        String correlationId,
        String occurredAt,
        String payload
) {
    public static EventLogResponse from(EventLog e) {
        return new EventLogResponse(
                e.getEventId(),
                e.getEventType(),
                e.getCorrelationId(),
                e.getOccurredAt().toString(),
                e.getPayload()
        );
    }
}
