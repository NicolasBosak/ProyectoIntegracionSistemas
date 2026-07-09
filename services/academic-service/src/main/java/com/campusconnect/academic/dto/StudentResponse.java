package com.campusconnect.academic.dto;

import com.campusconnect.academic.domain.Student;

/** Ficha del estudiante. */
public record StudentResponse(
        String studentId,
        String firstName,
        String lastName,
        String schoolId,
        String grade,
        String guardianEmail,
        String academicStatus,
        String financialStatus
) {
    public static StudentResponse from(Student s) {
        return new StudentResponse(
                s.getCode(),
                s.getFirstName(),
                s.getLastName(),
                s.getSchoolId(),
                s.getGrade(),
                s.getGuardianEmail(),
                s.getAcademicStatus(),
                s.getFinancialStatus().name()
        );
    }
}
