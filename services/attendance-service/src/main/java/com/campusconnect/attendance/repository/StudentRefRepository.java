package com.campusconnect.attendance.repository;

import com.campusconnect.attendance.domain.StudentRef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRefRepository extends JpaRepository<StudentRef, String> {
}
