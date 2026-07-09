package com.campusconnect.academic.event;

/** Datos especificos del evento StudentEnrolled (ver docs/02-contratos-eventos.md). */
public record StudentEnrolledData(
        String studentId,
        String schoolId,
        String firstName,
        String lastName,
        String grade,
        String enrollmentId,
        String guardianEmail
) {}
