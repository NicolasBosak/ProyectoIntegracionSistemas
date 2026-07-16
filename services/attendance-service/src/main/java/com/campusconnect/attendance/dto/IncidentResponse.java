package com.campusconnect.attendance.dto;

import com.campusconnect.attendance.domain.Incident;

public record IncidentResponse(
        String incidentId,
        String studentId,
        String category,
        String severity,
        String description,
        String reportedBy,
        String correlationId
) {
    public static IncidentResponse from(Incident i) {
        return new IncidentResponse(i.getCode(), i.getStudentId(), i.getCategory(),
                i.getSeverity(), i.getDescription(), i.getReportedBy(), i.getCorrelationId());
    }
}
