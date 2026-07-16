package com.campusconnect.notification.event;

/** Datos de los eventos NotificationSent / NotificationFailed. */
public record NotificationEventData(
        String notificationId,
        String studentId,
        String channel,
        String triggerEvent,
        String status,
        String reason
) {}
