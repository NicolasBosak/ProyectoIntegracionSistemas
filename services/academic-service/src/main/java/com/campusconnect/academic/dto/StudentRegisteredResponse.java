package com.campusconnect.academic.dto;

/** Respuesta al registrar un estudiante (incluye la matricula creada). */
public record StudentRegisteredResponse(
        String studentId,
        String enrollmentId,
        String financialStatus,
        String correlationId
) {}
