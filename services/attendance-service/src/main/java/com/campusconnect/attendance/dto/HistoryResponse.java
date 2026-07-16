package com.campusconnect.attendance.dto;

import java.util.List;

/** Historial combinado de asistencia e incidentes de un estudiante. */
public record HistoryResponse(
        String studentId,
        List<AttendanceResponse> attendance,
        List<IncidentResponse> incidents
) {}
