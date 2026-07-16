package com.campusconnect.attendance.event;

/** Datos del evento AttendanceRecorded. */
public record AttendanceRecordedData(
        String attendanceId,
        String studentId,
        String schoolId,
        String date,
        String status,
        String recordedBy
) {}
