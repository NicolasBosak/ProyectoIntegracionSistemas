package com.campusconnect.payment.dto;

import com.campusconnect.payment.domain.StudentRef;

public record StudentRefResponse(
        String studentId,
        String firstName,
        String lastName,
        String schoolId,
        String grade
) {
    public static StudentRefResponse from(StudentRef s) {
        return new StudentRefResponse(
                s.getStudentId(), s.getFirstName(), s.getLastName(), s.getSchoolId(), s.getGrade());
    }
}
