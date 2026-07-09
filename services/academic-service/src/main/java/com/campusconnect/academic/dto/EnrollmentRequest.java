package com.campusconnect.academic.dto;

import jakarta.validation.constraints.NotBlank;

/** Actualiza/confirma la matricula de un estudiante ya registrado. */
public record EnrollmentRequest(
        @NotBlank String grade,
        String schoolId
) {}
