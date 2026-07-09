package com.campusconnect.academic.repository;

import com.campusconnect.academic.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByCode(String code);
    boolean existsByCode(String code);
}
