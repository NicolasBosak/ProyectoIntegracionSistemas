package com.campusconnect.academic.listener;

import com.campusconnect.academic.config.RabbitMQConfig;
import com.campusconnect.academic.event.IncomingEvent;
import com.campusconnect.academic.service.AcademicService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consume PaymentConfirmed para actualizar el estado financiero del estudiante. */
@Component
public class PaymentEventListener {

    private final AcademicService service;

    public PaymentEventListener(AcademicService service) {
        this.service = service;
    }

    @RabbitListener(queues = RabbitMQConfig.Q_PAYMENT)
    public void onPaymentConfirmed(IncomingEvent event) {
        service.handlePaymentConfirmed(event);
    }
}
