package com.campusconnect.attendance.dto;

import jakarta.validation.constraints.NotBlank;

public record IncidentRequest(
        @NotBlank String studentId,
        @NotBlank String category,
        @NotBlank String severity,   // LOW | MEDIUM | HIGH
        String description,
        String reportedBy
) {}
