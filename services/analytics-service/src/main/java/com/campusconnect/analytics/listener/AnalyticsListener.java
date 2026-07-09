package com.campusconnect.analytics.listener;

import com.campusconnect.analytics.config.RabbitMQConfig;
import com.campusconnect.analytics.event.IncomingEvent;
import com.campusconnect.analytics.service.AnalyticsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Unico consumidor de la cola q.analytics.all (binding "#"): recibe todos los eventos
 * del ecosistema y los consolida en el read model.
 */
@Component
public class AnalyticsListener {

    private final AnalyticsService service;

    public AnalyticsListener(AnalyticsService service) {
        this.service = service;
    }

    @RabbitListener(queues = RabbitMQConfig.Q_ALL)
    public void onEvent(IncomingEvent event) {
        service.handleEvent(event);
    }
}
