package com.campusconnect.analytics.dto;

import com.campusconnect.analytics.domain.EventRecord;

public record EventRecordResponse(
        String eventId,
        String eventType,
        String correlationId,
        String source,
        String occurredAt,
        String receivedAt
) {
    public static EventRecordResponse from(EventRecord e) {
        return new EventRecordResponse(
                e.getEventId(), e.getEventType(), e.getCorrelationId(),
                e.getSource(), e.getOccurredAt(), e.getReceivedAt().toString());
    }
}
