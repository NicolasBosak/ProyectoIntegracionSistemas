package com.campusconnect.academic.repository;

import com.campusconnect.academic.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentId(Long studentId);
}
