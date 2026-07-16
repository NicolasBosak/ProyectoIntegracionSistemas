package com.campusconnect.analytics.event;

import java.util.Map;

/** Evento entrante generico (envelope estandar). */
public record IncomingEvent(
        String eventId,
        String eventType,
        String occurredAt,
        String correlationId,
        String source,
        String version,
        Map<String, Object> data
) {}
