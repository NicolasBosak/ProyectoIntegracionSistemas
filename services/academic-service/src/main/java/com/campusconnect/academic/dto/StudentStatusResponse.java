package com.campusconnect.academic.dto;

/** Estado academico y financiero consolidado del estudiante. */
public record StudentStatusResponse(
        String studentId,
        String academicStatus,
        String financialStatus
) {}
