package com.campusconnect.attendance.dto;

import com.campusconnect.attendance.domain.AttendanceRecord;

public record AttendanceResponse(
        String attendanceId,
        String studentId,
        String date,
        String status,
        String recordedBy,
        String correlationId
) {
    public static AttendanceResponse from(AttendanceRecord a) {
        return new AttendanceResponse(a.getCode(), a.getStudentId(),
                a.getDate().toString(), a.getStatus(), a.getRecordedBy(), a.getCorrelationId());
    }
}
