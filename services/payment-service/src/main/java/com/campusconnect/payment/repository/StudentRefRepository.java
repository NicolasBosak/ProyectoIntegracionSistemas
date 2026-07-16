package com.campusconnect.payment.repository;

import com.campusconnect.payment.domain.StudentRef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRefRepository extends JpaRepository<StudentRef, String> {
}
