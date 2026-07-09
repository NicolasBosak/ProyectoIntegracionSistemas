package com.campusconnect.analytics.repository;

import com.campusconnect.analytics.domain.EventRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRecordRepository extends JpaRepository<EventRecord, Long> {
    List<EventRecord> findTop100ByOrderByReceivedAtDesc();
    boolean existsByEventId(String eventId);
}
