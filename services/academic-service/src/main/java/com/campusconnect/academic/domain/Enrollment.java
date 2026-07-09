package com.campusconnect.academic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Codigo de negocio legible: ENR-001. */
    @Column(unique = true)
    private String code;

    private Long studentId;
    private String schoolId;
    private String grade;
    private LocalDate enrollmentDate;
    private String status = "ACTIVE";

    protected Enrollment() {
        // requerido por JPA
    }

    public Enrollment(Long studentId, String schoolId, String grade) {
        this.studentId = studentId;
        this.schoolId = schoolId;
        this.grade = grade;
        this.enrollmentDate = LocalDate.now();
        this.status = "ACTIVE";
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getStudentId() { return studentId; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
