package com.duoc.cursos.bff.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// Configuracion de RabbitMQ para publicar eventos academicos desde el BFF.
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${app.rabbitmq.error-queue}")
    private String errorQueueName;

    @Value("${app.rabbitmq.error-routing-key}")
    private String errorRoutingKey;

    @Bean
    // Configura la cola principal como durable para conservar los eventos.
    Queue eventosAcademicosQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    // Reserva una cola separada para los mensajes reenviados con error.
    Queue eventosAcademicosErrorQueue() {
        return new Queue(errorQueueName, true);
    }

    @Bean
    // Declara el exchange directo usado por el flujo.
    DirectExchange eventosAcademicosExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    Binding eventosAcademicosBinding(Queue eventosAcademicosQueue, DirectExchange eventosAcademicosExchange) {
        return BindingBuilder.bind(eventosAcademicosQueue).to(eventosAcademicosExchange).with(routingKey);
    }

    @Bean
    Binding eventosAcademicosErrorBinding(Queue eventosAcademicosErrorQueue, DirectExchange eventosAcademicosExchange) {
        return BindingBuilder.bind(eventosAcademicosErrorQueue).to(eventosAcademicosExchange).with(errorRoutingKey);
    }

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    // Publica mensajes persistentes y serializados como JSON.
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);
        rabbitTemplate.setBeforePublishPostProcessors(message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
        return rabbitTemplate;
    }
}
