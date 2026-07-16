package com.campusconnect.attendance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;              // INC-001

    private String studentId;
    private String schoolId;
    private String category;          // BEHAVIOR, HEALTH, ...
    private String severity;          // LOW | MEDIUM | HIGH

    @Column(columnDefinition = "text")
    private String description;

    private String reportedBy;
    private String correlationId;
    private Instant createdAt = Instant.now();

    protected Incident() {
    }

    public Incident(String studentId, String schoolId, String category, String severity,
                    String description, String reportedBy, String correlationId) {
        this.studentId = studentId;
        this.schoolId = schoolId;
        this.category = category;
        this.severity = severity;
        this.description = description;
        this.reportedBy = reportedBy;
        this.correlationId = correlationId;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStudentId() { return studentId; }
    public String getSchoolId() { return schoolId; }
    public String getCategory() { return category; }
    public String getSeverity() { return severity; }
    public String getDescription() { return description; }
    public String getReportedBy() { return reportedBy; }
    public String getCorrelationId() { return correlationId; }
    public Instant getCreatedAt() { return createdAt; }
}
