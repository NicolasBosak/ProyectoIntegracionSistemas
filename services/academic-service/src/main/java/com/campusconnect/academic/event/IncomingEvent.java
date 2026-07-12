package com.campusconnect.academic.event;

import java.util.Map;

/** Evento entrante generico (envelope estandar) para los consumidores del servicio. */
public record IncomingEvent(
        String eventId,
        String eventType,
        String occurredAt,
        String correlationId,
        String source,
        String version,
        Map<String, Object> data
) {
    public String dataString(String key) {
        Object v = data == null ? null : data.get(key);
        return v == null ? null : v.toString();
    }
}
