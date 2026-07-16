package com.campusconnect.notification.service;

import com.campusconnect.notification.domain.DeadLetter;
import com.campusconnect.notification.domain.Notification;
import com.campusconnect.notification.dto.NotificationResponse;
import com.campusconnect.notification.event.EventEnvelope;
import com.campusconnect.notification.event.EventPublisher;
import com.campusconnect.notification.event.IncomingEvent;
import com.campusconnect.notification.event.NotificationEventData;
import com.campusconnect.notification.repository.DeadLetterRepository;
import com.campusconnect.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Genera notificaciones simuladas a partir de eventos y publica NotificationSent/Failed.
 * Incluye la logica de resiliencia: modo caos (para provocar fallos), captura de mensajes
 * en la DLQ y reproceso manual.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String CHANNEL = "SIMULATED_EMAIL";

    private final NotificationRepository repository;
    private final DeadLetterRepository deadLetters;
    private final EventPublisher publisher;
    private final ObjectMapper objectMapper;
    private final boolean chaosEnabled;

    public NotificationService(NotificationRepository repository,
                               DeadLetterRepository deadLetters,
                               EventPublisher publisher,
                               ObjectMapper objectMapper,
                               @Value("${campus.notifications.chaos:false}") boolean chaosEnabled) {
        this.repository = repository;
        this.deadLetters = deadLetters;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
        this.chaosEnabled = chaosEnabled;
    }

    /**
     * Procesa un evento entrante. En modo caos, los incidentes fallan a proposito para
     * demostrar reintentos + DLQ. Si falla, se relanza para que RabbitMQ reintente/derive a DLQ.
     */
    @Transactional
    public void handleEvent(IncomingEvent event) {
        if (chaosEnabled && "IncidentReported".equals(event.eventType())) {
            log.warn("[CHAOS] Falla simulada procesando {} correlationId={}",
                    event.eventType(), event.correlationId());
            throw new IllegalStateException("Fallo simulado (chaos) al notificar un incidente");
        }
        processNotification(event);
    }

    /** Trabajo real de notificacion (sin caos). Lo usa el flujo normal y el reproceso. */
    @Transactional
    public void processNotification(IncomingEvent event) {
        String studentId = event.dataString("studentId");
        Notification notification = new Notification(
                studentId, CHANNEL, event.eventType(), event.correlationId(),
                buildMessage(event), "SENT", null);
        notification = repository.save(notification);
        notification.setCode("NOT-" + String.format("%03d", notification.getId()));

        NotificationEventData data = new NotificationEventData(notification.getCode(), studentId,
                CHANNEL, event.eventType(), "SENT", null);
        publisher.publish("notification.sent", EventEnvelope.of("NotificationSent", event.correlationId(), data));

        log.info("Notificacion simulada enviada code={} trigger={} correlationId={}",
                notification.getCode(), event.eventType(), event.correlationId());
    }

    /** Registra un mensaje que llegó a la DLQ y publica NotificationFailed. */
    @Transactional
    public void captureDeadLetter(IncomingEvent event, String reason) {
        String studentId = event.dataString("studentId");
        deadLetters.save(new DeadLetter(event.eventId(), event.eventType(), studentId,
                event.correlationId(), reason, serialize(event)));

        NotificationEventData data = new NotificationEventData(null, studentId, CHANNEL,
                event.eventType(), "FAILED", reason);
        publisher.publish("notification.failed",
                EventEnvelope.of("NotificationFailed", event.correlationId(), data));

        log.warn("Mensaje en DLQ registrado eventId={} type={} correlationId={}",
                event.eventId(), event.eventType(), event.correlationId());
    }

    /** Reprocesa un mensaje fallido desde la DLQ (sin caos). */
    @Transactional
    public void reprocess(Long deadLetterId) {
        DeadLetter dl = deadLetters.findById(deadLetterId)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje fallido no encontrado: " + deadLetterId));
        if ("REPROCESSED".equals(dl.getStatus())) {
            return; // ya reprocesado (idempotente)
        }
        IncomingEvent event = deserialize(dl.getPayload());
        processNotification(event);
        dl.setStatus("REPROCESSED");
        log.info("Mensaje reprocesado deadLetterId={} correlationId={}", deadLetterId, dl.getCorrelationId());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listFailed() {
        return repository.findByStatusOrderByCreatedAtDesc("FAILED").stream().map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<DeadLetter> listDeadLetters() {
        return deadLetters.findAllByOrderByCreatedAtDesc();
    }

    private String buildMessage(IncomingEvent event) {
        String studentId = event.dataString("studentId");
        return switch (event.eventType()) {
            case "StudentEnrolled" -> "Bienvenido/a " + event.dataString("firstName") + " "
                    + event.dataString("lastName") + ". Matricula " + event.dataString("enrollmentId") + " registrada.";
            case "PaymentConfirmed" -> "Pago confirmado de " + event.dataString("amount")
                    + " para el estudiante " + studentId + ".";
            case "IncidentReported" -> "Se registro un incidente (" + event.dataString("severity")
                    + ") para el estudiante " + studentId + ".";
            default -> "Notificacion para el estudiante " + studentId + ".";
        };
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo serializar el evento", e);
        }
    }

    private IncomingEvent deserialize(String json) {
        try {
            return objectMapper.readValue(json, IncomingEvent.class);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo deserializar el evento", e);
        }
    }
}
