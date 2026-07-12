package com.campusconnect.academic.event;

/** Datos del evento StudentStatusUpdated. */
public record StudentStatusUpdatedData(
        String studentId,
        String financialStatus,
        String previousStatus,
        String reason
) {}
