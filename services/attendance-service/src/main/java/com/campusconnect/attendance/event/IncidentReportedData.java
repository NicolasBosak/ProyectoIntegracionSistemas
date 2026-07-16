package com.campusconnect.attendance.event;

/** Datos del evento IncidentReported. */
public record IncidentReportedData(
        String incidentId,
        String studentId,
        String schoolId,
        String category,
        String severity,
        String description,
        String reportedBy
) {}
