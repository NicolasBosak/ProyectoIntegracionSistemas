package com.campusconnect.notification.event;

import java.time.Instant;
import java.util.UUID;

/** Envelope estandar para los eventos que publica este servicio (NotificationSent/Failed). */
public record EventEnvelope<T>(
        String eventId,
        String eventType,
        String occurredAt,
        String correlationId,
        String source,
        String version,
        T data
) {
    public static <T> EventEnvelope<T> of(String eventType, String correlationId, T data) {
        return new EventEnvelope<>(
                "evt-" + UUID.randomUUID(),
                eventType,
                Instant.now().toString(),
                correlationId,
                "notification-service",
                "1.0",
                data
        );
    }
}
