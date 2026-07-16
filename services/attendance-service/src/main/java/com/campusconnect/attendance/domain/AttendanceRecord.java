package com.campusconnect.attendance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;              // ATT-001

    private String studentId;
    private String schoolId;
    private LocalDate date;
    private String status;            // PRESENT | ABSENT | LATE
    private String recordedBy;
    private String correlationId;
    private Instant createdAt = Instant.now();

    protected AttendanceRecord() {
    }

    public AttendanceRecord(String studentId, String schoolId, LocalDate date, String status,
                            String recordedBy, String correlationId) {
        this.studentId = studentId;
        this.schoolId = schoolId;
        this.date = date;
        this.status = status;
        this.recordedBy = recordedBy;
        this.correlationId = correlationId;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStudentId() { return studentId; }
    public String getSchoolId() { return schoolId; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    public String getRecordedBy() { return recordedBy; }
    public String getCorrelationId() { return correlationId; }
    public Instant getCreatedAt() { return createdAt; }
}
