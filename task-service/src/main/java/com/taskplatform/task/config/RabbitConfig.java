package com.taskplatform.task.config;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE_NAME = "task.events";
    public static final String QUEUE_NAME = "notification.queue";
    public static final String ROUTING_KEY = "task.created";


    @Bean
    public TopicExchange taskExchange(){
      return new TopicExchange(EXCHANGE_NAME);
    }
    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE_NAME, true); // durable = true
    }

    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange taskExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(taskExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConvertor(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "rabbitTemplate")
    @Primary
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConvertor){
        RabbitTemplate template =  new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConvertor);
        return template;
    }
}
