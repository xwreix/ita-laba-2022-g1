package com.itechart.identity_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistrationMessagingConfig
{
    public static final String QUEUE = "registration_email_queue";
    public static final String EXCHANGE = "exchange";
    public static final String ROUTING_KEY = "registration_email_routing_key";

    @Bean
    public Queue queue1() { return new Queue(QUEUE); }

    @Bean
    public TopicExchange exchange() { return new TopicExchange(EXCHANGE); }

    @Bean
    public Binding binding1(TopicExchange exchange)
    {
        return BindingBuilder.bind(queue1()).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter() { return new Jackson2JsonMessageConverter(new ObjectMapper()); }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory)
    {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}