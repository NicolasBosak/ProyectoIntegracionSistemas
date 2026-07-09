package com.campusconnect.notification.dto;

import com.campusconnect.notification.domain.Notification;

public record NotificationResponse(
        String notificationId,
        String studentId,
        String channel,
        String triggerEvent,
        String message,
        String status,
        String reason,
        String correlationId,
        String createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getCode(), n.getStudentId(), n.getChannel(), n.getTriggerEvent(),
                n.getMessage(), n.getStatus(), n.getReason(), n.getCorrelationId(),
                n.getCreatedAt().toString());
    }
}
