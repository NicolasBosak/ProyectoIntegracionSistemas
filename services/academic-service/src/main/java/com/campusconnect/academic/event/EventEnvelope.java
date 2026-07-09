package com.campusconnect.academic.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Envoltura estandar de todos los eventos de negocio del ecosistema
 * (ver docs/02-contratos-eventos.md). Los datos especificos van en {@code data}.
 */
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
                "academic-service",
                "1.0",
                data
        );
    }
}
