package com.campusconnect.academic.repository;

import com.campusconnect.academic.domain.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    List<EventLog> findByStudentIdOrderByOccurredAtDesc(Long studentId);
}
