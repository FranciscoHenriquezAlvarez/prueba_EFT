package com.duoc.eventos.academicos.consumidor.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// Configuracion de RabbitMQ para el consumo y manejo de errores del microservicio.
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.error-queue}")
    private String errorQueueName;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${app.rabbitmq.error-routing-key}")
    private String errorRoutingKey;

    @Bean
    Queue eventosAcademicosQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    Queue eventosAcademicosErrorQueue() {
        return new Queue(errorQueueName, true);
    }

    @Bean
    DirectExchange eventosAcademicosExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    Binding eventosAcademicosBinding(Queue eventosAcademicosQueue, DirectExchange eventosAcademicosExchange) {
        return BindingBuilder.bind(eventosAcademicosQueue).to(eventosAcademicosExchange).with(routingKey);
    }

    @Bean
    Binding eventosAcademicosErrorBinding(Queue eventosAcademicosErrorQueue,
                                          DirectExchange eventosAcademicosExchange) {
        return BindingBuilder.bind(eventosAcademicosErrorQueue).to(eventosAcademicosExchange).with(errorRoutingKey);
    }

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);
        rabbitTemplate.setBeforePublishPostProcessors(message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
        return rabbitTemplate;
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(rabbitMessageConverter);
        return factory;
    }
}
