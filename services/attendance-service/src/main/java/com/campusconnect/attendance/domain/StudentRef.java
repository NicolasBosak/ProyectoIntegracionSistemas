package com.campusconnect.attendance.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Proyeccion local de estudiantes, construida al consumir StudentEnrolled. */
@Entity
@Table(name = "student_refs")
public class StudentRef {

    @Id
    private String studentId;
    private String firstName;
    private String lastName;
    private String schoolId;
    private String grade;

    protected StudentRef() {
    }

    public StudentRef(String studentId, String firstName, String lastName, String schoolId, String grade) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolId = schoolId;
        this.grade = grade;
    }

    public String getStudentId() { return studentId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getSchoolId() { return schoolId; }
    public String getGrade() { return grade; }
}
