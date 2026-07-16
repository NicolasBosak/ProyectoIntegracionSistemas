package com.campusconnect.notification.listener;

import com.campusconnect.notification.config.RabbitMQConfig;
import com.campusconnect.notification.event.IncomingEvent;
import com.campusconnect.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consume las Dead Letter Queues: registra el mensaje fallido (estado consultable) y publica
 * NotificationFailed. Evidencia el patrón Dead Letter Channel.
 */
@Component
public class DeadLetterListener {

    private final NotificationService service;

    public DeadLetterListener(NotificationService service) {
        this.service = service;
    }

    @RabbitListener(queues = {
            RabbitMQConfig.Q_STUDENT + ".dlq",
            RabbitMQConfig.Q_PAYMENT + ".dlq",
            RabbitMQConfig.Q_INCIDENT + ".dlq"
    })
    public void onDeadLetter(IncomingEvent event) {
        service.captureDeadLetter(event, "Procesamiento fallido tras reintentos");
    }
}
