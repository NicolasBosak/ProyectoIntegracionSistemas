package com.campusconnect.academic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateStudentRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String schoolId,
        @NotBlank String grade,
        @Email String guardianEmail
) {}
