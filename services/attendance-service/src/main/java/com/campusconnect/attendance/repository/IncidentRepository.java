package com.campusconnect.attendance.repository;

import com.campusconnect.attendance.domain.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStudentIdOrderByCreatedAtDesc(String studentId);
}
