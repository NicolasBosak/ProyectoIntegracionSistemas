package com.campusconnect.notification.service;

import com.campusconnect.notification.domain.Notification;
import com.campusconnect.notification.dto.NotificationResponse;
import com.campusconnect.notification.event.EventEnvelope;
import com.campusconnect.notification.event.EventPublisher;
import com.campusconnect.notification.event.IncomingEvent;
import com.campusconnect.notification.event.NotificationEventData;
import com.campusconnect.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Genera notificaciones simuladas a partir de eventos de negocio y publica
 * NotificationSent / NotificationFailed. El canal es simulado (no envia correo real).
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String CHANNEL = "SIMULATED_EMAIL";

    private final NotificationRepository repository;
    private final EventPublisher publisher;

    public NotificationService(NotificationRepository repository, EventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    /** Procesa un evento entrante creando una notificacion simulada. */
    @Transactional
    public void handleEvent(IncomingEvent event) {
        String studentId = event.dataString("studentId");
        String message = buildMessage(event);

        Notification notification = new Notification(
                studentId, CHANNEL, event.eventType(), event.correlationId(),
                message, "SENT", null);
        notification = repository.save(notification);
        notification.setCode("NOT-" + String.format("%03d", notification.getId()));

        publishOutcome(notification, "NotificationSent", "notification.sent");
        log.info("Notificacion simulada enviada code={} trigger={} correlationId={}",
                notification.getCode(), event.eventType(), event.correlationId());
    }

    private void publishOutcome(Notification n, String eventType, String routingKey) {
        NotificationEventData data = new NotificationEventData(
                n.getCode(), n.getStudentId(), n.getChannel(),
                n.getTriggerEvent(), n.getStatus(), n.getReason());
        EventEnvelope<NotificationEventData> event =
                EventEnvelope.of(eventType, n.getCorrelationId(), data);
        publisher.publish(routingKey, event);
    }

    private String buildMessage(IncomingEvent event) {
        String studentId = event.dataString("studentId");
        return switch (event.eventType()) {
            case "StudentEnrolled" -> "Bienvenido/a " + event.dataString("firstName") + " "
                    + event.dataString("lastName") + ". Matricula " + event.dataString("enrollmentId")
                    + " registrada.";
            case "PaymentConfirmed" -> "Pago confirmado de " + event.dataString("amount")
                    + " para el estudiante " + studentId + ".";
            case "IncidentReported" -> "Se registro un incidente (" + event.dataString("severity")
                    + ") para el estudiante " + studentId + ".";
            default -> "Notificacion para el estudiante " + studentId + ".";
        };
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(NotificationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listFailed() {
        return repository.findByStatusOrderByCreatedAtDesc("FAILED").stream()
                .map(NotificationResponse::from).toList();
    }
}
