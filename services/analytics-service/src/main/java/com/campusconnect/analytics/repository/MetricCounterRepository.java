package com.campusconnect.analytics.repository;

import com.campusconnect.analytics.domain.MetricCounter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricCounterRepository extends JpaRepository<MetricCounter, String> {
}
