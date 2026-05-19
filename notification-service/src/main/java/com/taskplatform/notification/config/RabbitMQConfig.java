package com.taskplatform.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "task.events";
    public static final String QUEUE_NAME = "notification.queue";
    public static final String ROUTING_KEY = "task.created";

    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange taskExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(taskExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConvertor() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConvertor
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConvertor);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

}
