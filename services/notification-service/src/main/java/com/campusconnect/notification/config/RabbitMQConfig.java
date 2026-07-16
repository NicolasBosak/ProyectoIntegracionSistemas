package com.campusconnect.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia de mensajeria del servicio de Notificaciones (ver docs/04-mensajeria-rabbitmq.md).
 * Consume eventos del topic exchange central; los mensajes fallidos van a la Dead Letter Queue.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EVENTS_EXCHANGE = "campus.events";
    public static final String DLX = "campus.dlx";

    public static final String Q_STUDENT = "q.notifications.student";
    public static final String Q_PAYMENT = "q.notifications.payment";
    public static final String Q_INCIDENT = "q.notifications.incident";

    // -------- Exchanges --------
    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DLX, true, false);
    }

    // -------- Colas de trabajo (con Dead Letter Channel) --------
    @Bean
    public Queue studentQueue() {
        return workQueue(Q_STUDENT);
    }

    @Bean
    public Queue paymentQueue() {
        return workQueue(Q_PAYMENT);
    }

    @Bean
    public Queue incidentQueue() {
        return workQueue(Q_INCIDENT);
    }

    private Queue workQueue(String name) {
        return QueueBuilder.durable(name)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", name + ".dlq")
                .build();
    }

    // -------- Dead Letter Queues --------
    @Bean
    public Queue studentDlq() {
        return QueueBuilder.durable(Q_STUDENT + ".dlq").build();
    }

    @Bean
    public Queue paymentDlq() {
        return QueueBuilder.durable(Q_PAYMENT + ".dlq").build();
    }

    @Bean
    public Queue incidentDlq() {
        return QueueBuilder.durable(Q_INCIDENT + ".dlq").build();
    }

    // -------- Bindings de trabajo (routing key = tipo de evento) --------
    @Bean
    public Binding studentBinding() {
        return BindingBuilder.bind(studentQueue()).to(eventsExchange()).with("student.enrolled");
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue()).to(eventsExchange()).with("payment.confirmed");
    }

    @Bean
    public Binding incidentBinding() {
        return BindingBuilder.bind(incidentQueue()).to(eventsExchange()).with("incident.reported");
    }

    // -------- Bindings de las DLQ --------
    @Bean
    public Binding studentDlqBinding() {
        return BindingBuilder.bind(studentDlq()).to(deadLetterExchange()).with(Q_STUDENT + ".dlq");
    }

    @Bean
    public Binding paymentDlqBinding() {
        return BindingBuilder.bind(paymentDlq()).to(deadLetterExchange()).with(Q_PAYMENT + ".dlq");
    }

    @Bean
    public Binding incidentDlqBinding() {
        return BindingBuilder.bind(incidentDlq()).to(deadLetterExchange()).with(Q_INCIDENT + ".dlq");
    }

    // -------- Conversor JSON (modo INFERRED: usa el tipo del listener, no el header __TypeId__) --------
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
