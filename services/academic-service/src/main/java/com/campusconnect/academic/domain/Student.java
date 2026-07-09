package com.campusconnect.academic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Codigo de negocio legible: STU-001. Se asigna tras persistir. */
    @Column(unique = true)
    private String code;

    private String firstName;
    private String lastName;
    private String schoolId;
    private String grade;
    private String guardianEmail;

    @Enumerated(EnumType.STRING)
    private FinancialStatus financialStatus = FinancialStatus.PENDING;

    private String academicStatus = "ACTIVE";

    private Instant createdAt = Instant.now();

    protected Student() {
        // requerido por JPA
    }

    public Student(String firstName, String lastName, String schoolId, String grade, String guardianEmail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolId = schoolId;
        this.grade = grade;
        this.guardianEmail = guardianEmail;
        this.financialStatus = FinancialStatus.PENDING;
        this.academicStatus = "ACTIVE";
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getGuardianEmail() { return guardianEmail; }
    public void setGuardianEmail(String guardianEmail) { this.guardianEmail = guardianEmail; }
    public FinancialStatus getFinancialStatus() { return financialStatus; }
    public void setFinancialStatus(FinancialStatus financialStatus) { this.financialStatus = financialStatus; }
    public String getAcademicStatus() { return academicStatus; }
    public void setAcademicStatus(String academicStatus) { this.academicStatus = academicStatus; }
    public Instant getCreatedAt() { return createdAt; }
}
