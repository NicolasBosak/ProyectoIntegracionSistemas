package com.campusconnect.notification.event;

import java.util.Map;

/**
 * Representacion generica de un evento entrante (envelope estandar). Los datos
 * especificos quedan en {@code data} como un mapa, para consumir cualquier tipo de evento.
 */
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
