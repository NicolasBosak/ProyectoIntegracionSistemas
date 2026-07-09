package com.campusconnect.academic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declara el topic exchange central del ecosistema y configura el RabbitTemplate
 * para publicar los eventos como JSON. El servicio Academico es PRODUCTOR de eventos;
 * las colas y bindings los declara cada consumidor (Notificaciones, Analitica).
 */
@Configuration
public class RabbitMQConfig {

    @Value("${campus.messaging.exchange}")
    private String exchangeName;

    @Bean
    public TopicExchange campusEventsExchange() {
        // durable = true, autoDelete = false
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
