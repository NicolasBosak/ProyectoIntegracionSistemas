package com.campusconnect.payment.service;

import com.campusconnect.payment.domain.Payment;
import com.campusconnect.payment.domain.PaymentStatus;
import com.campusconnect.payment.domain.StudentRef;
import com.campusconnect.payment.dto.ConfirmPaymentRequest;
import com.campusconnect.payment.dto.PaymentResponse;
import com.campusconnect.payment.dto.RegisterDebtRequest;
import com.campusconnect.payment.dto.StudentRefResponse;
import com.campusconnect.payment.event.EventEnvelope;
import com.campusconnect.payment.event.EventPublisher;
import com.campusconnect.payment.event.IncomingEvent;
import com.campusconnect.payment.event.PaymentConfirmedData;
import com.campusconnect.payment.repository.PaymentRepository;
import com.campusconnect.payment.repository.StudentRefRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository payments;
    private final StudentRefRepository studentRefs;
    private final EventPublisher publisher;
    private final BigDecimal defaultTuition;
    private final String currency;
    private final String paymentConfirmedRoutingKey;

    public PaymentService(PaymentRepository payments,
                          StudentRefRepository studentRefs,
                          EventPublisher publisher,
                          @Value("${campus.payments.default-tuition}") BigDecimal defaultTuition,
                          @Value("${campus.payments.currency}") String currency,
                          @Value("${campus.messaging.routing-keys.payment-confirmed}") String paymentConfirmedRoutingKey) {
        this.payments = payments;
        this.studentRefs = studentRefs;
        this.publisher = publisher;
        this.defaultTuition = defaultTuition;
        this.currency = currency;
        this.paymentConfirmedRoutingKey = paymentConfirmedRoutingKey;
    }

    /**
     * Al matricularse un estudiante (StudentEnrolled): guarda su referencia local y crea la
     * deuda de matricula pendiente. Idempotente: si el estudiante ya existe, no duplica.
     */
    @Transactional
    public void handleStudentEnrolled(IncomingEvent event) {
        String studentId = event.dataString("studentId");
        if (studentId == null || studentRefs.existsById(studentId)) {
            return; // ya procesado o dato invalido
        }
        studentRefs.save(new StudentRef(studentId, event.dataString("firstName"),
                event.dataString("lastName"), event.dataString("schoolId"), event.dataString("grade")));

        Payment debt = new Payment(studentId, "Matricula", defaultTuition, currency,
                PaymentStatus.PENDING, event.correlationId());
        debt = payments.save(debt);
        debt.setCode("PAY-" + String.format("%03d", debt.getId()));

        log.info("Deuda de matricula creada studentId={} monto={} correlationId={}",
                studentId, defaultTuition, event.correlationId());
    }

    /** Confirma un pago y publica PaymentConfirmed. */
    @Transactional
    public PaymentResponse confirmPayment(ConfirmPaymentRequest req) {
        String studentId = req.studentId();
        Payment payment = payments
                .findFirstByStudentIdAndStatusOrderByCreatedAtAsc(studentId, PaymentStatus.PENDING)
                .orElseGet(() -> {
                    // No hay deuda pendiente: se crea un pago directo
                    Payment p = new Payment(studentId,
                            req.concept() != null ? req.concept() : "Pago",
                            req.amount() != null ? req.amount() : defaultTuition,
                            currency, PaymentStatus.PENDING, newCorrelationId());
                    return payments.save(p);
                });

        if (req.amount() != null) {
            payment.setAmount(req.amount());
        }
        if (req.concept() != null && !req.concept().isBlank()) {
            payment.setConcept(req.concept());
        }
        payment.setMethod(req.method() != null ? req.method() : "TRANSFER");
        payment.setStatus(PaymentStatus.CONFIRMED);
        payment.setConfirmedAt(Instant.now());
        if (payment.getCorrelationId() == null) {
            payment.setCorrelationId(newCorrelationId());
        }
        if (payment.getCode() == null) {
            payment = payments.save(payment);
            payment.setCode("PAY-" + String.format("%03d", payment.getId()));
        }

        PaymentConfirmedData data = new PaymentConfirmedData(
                payment.getCode(), studentId, payment.getAmount(), payment.getCurrency(),
                payment.getConcept(), payment.getMethod(), payment.getConfirmedAt().toString());
        EventEnvelope<PaymentConfirmedData> event =
                EventEnvelope.of("PaymentConfirmed", payment.getCorrelationId(), data);
        publisher.publish(paymentConfirmedRoutingKey, event);

        log.info("Pago confirmado code={} studentId={} correlationId={}",
                payment.getCode(), studentId, payment.getCorrelationId());
        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse registerDebt(RegisterDebtRequest req) {
        Payment debt = new Payment(req.studentId(), req.concept(), req.amount(), currency,
                PaymentStatus.PENDING, newCorrelationId());
        debt = payments.save(debt);
        debt.setCode("PAY-" + String.format("%03d", debt.getId()));
        return PaymentResponse.from(debt);
    }

    @Transactional(readOnly = true)
    public List<StudentRefResponse> listStudents() {
        return studentRefs.findAll().stream().map(StudentRefResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listPending() {
        return payments.findByStatusOrderByCreatedAtDesc(PaymentStatus.PENDING)
                .stream().map(PaymentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listConfirmed() {
        return payments.findByStatusOrderByCreatedAtDesc(PaymentStatus.CONFIRMED)
                .stream().map(PaymentResponse::from).toList();
    }

    private String newCorrelationId() {
        String day = DateTimeFormatter.ofPattern("yyyyMMdd")
                .withZone(ZoneOffset.UTC).format(Instant.now());
        return "corr-" + day + "-" + Long.toString(System.nanoTime()).substring(6);
    }
}
