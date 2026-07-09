package com.campusconnect.notification.listener;

import com.campusconnect.notification.config.RabbitMQConfig;
import com.campusconnect.notification.event.IncomingEvent;
import com.campusconnect.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidores de eventos de negocio. Cada cola recibe un tipo de evento (Publish/Subscribe:
 * el mismo evento tambien lo consume Analitica en su propia cola).
 */
@Component
public class NotificationListener {

    private final NotificationService service;

    public NotificationListener(NotificationService service) {
        this.service = service;
    }

    @RabbitListener(queues = RabbitMQConfig.Q_STUDENT)
    public void onStudentEnrolled(IncomingEvent event) {
        service.handleEvent(event);
    }

    @RabbitListener(queues = RabbitMQConfig.Q_PAYMENT)
    public void onPaymentConfirmed(IncomingEvent event) {
        service.handleEvent(event);
    }

    @RabbitListener(queues = RabbitMQConfig.Q_INCIDENT)
    public void onIncidentReported(IncomingEvent event) {
        service.handleEvent(event);
    }
}
