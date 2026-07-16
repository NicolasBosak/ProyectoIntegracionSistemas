package com.campusconnect.attendance.dto;

import jakarta.validation.constraints.NotBlank;

public record AttendanceRequest(
        @NotBlank String studentId,
        String date,        // ISO yyyy-MM-dd; si es null se usa hoy
        @NotBlank String status,   // PRESENT | ABSENT | LATE
        String recordedBy
) {}
