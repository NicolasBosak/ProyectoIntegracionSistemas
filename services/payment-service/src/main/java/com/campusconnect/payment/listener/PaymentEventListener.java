package com.campusconnect.payment.listener;

import com.campusconnect.payment.config.RabbitMQConfig;
import com.campusconnect.payment.event.IncomingEvent;
import com.campusconnect.payment.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consume StudentEnrolled para crear la deuda de matricula y la proyeccion local. */
@Component
public class PaymentEventListener {

    private final PaymentService service;

    public PaymentEventListener(PaymentService service) {
        this.service = service;
    }

    @RabbitListener(queues = RabbitMQConfig.Q_STUDENT)
    public void onStudentEnrolled(IncomingEvent event) {
        service.handleStudentEnrolled(event);
    }
}
