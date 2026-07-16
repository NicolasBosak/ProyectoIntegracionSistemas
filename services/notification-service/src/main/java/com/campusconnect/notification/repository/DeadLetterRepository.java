package com.campusconnect.notification.repository;

import com.campusconnect.notification.domain.DeadLetter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeadLetterRepository extends JpaRepository<DeadLetter, Long> {
    List<DeadLetter> findAllByOrderByCreatedAtDesc();
}
