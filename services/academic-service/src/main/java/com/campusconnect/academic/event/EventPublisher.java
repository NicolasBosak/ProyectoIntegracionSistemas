package com.campusconnect.academic.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publica eventos de negocio al topic exchange central. La routing key determina
 * que consumidores lo reciben (Publish/Subscribe).
 */
@Component
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public EventPublisher(RabbitTemplate rabbitTemplate,
                          @Value("${campus.messaging.exchange}") String exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void publish(String routingKey, EventEnvelope<?> event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Evento publicado type={} routingKey={} correlationId={} eventId={}",
                event.eventType(), routingKey, event.correlationId(), event.eventId());
    }
}
