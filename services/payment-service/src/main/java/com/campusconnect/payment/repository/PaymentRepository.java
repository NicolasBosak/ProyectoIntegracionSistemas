package com.campusconnect.payment.repository;

import com.campusconnect.payment.domain.Payment;
import com.campusconnect.payment.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);
    List<Payment> findByStudentIdOrderByCreatedAtDesc(String studentId);
    Optional<Payment> findFirstByStudentIdAndStatusOrderByCreatedAtAsc(String studentId, PaymentStatus status);
}
