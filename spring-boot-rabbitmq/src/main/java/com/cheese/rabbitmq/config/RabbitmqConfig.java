package com.cheese.rabbitmq.config;

import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

/**
 * rabbit配置
 * <p>
 * confirm 消息发送至broker结果的确认 实现原理: 基于异步的ConfirmListener PublisherCallbackChannelImpl
 * CachingConnectionFactory内置了一个生产PublisherCallbackChannelImpl实例的工厂 PublisherCallbackChannelImpl是ConfirmListener的实现
 * PublisherCallbackChannelImpl的handleAck和handleNack最终调用了RabbitTemplate.ConfirmCallback.confirm方法
 * 参考rabbitmq-api d04_broker_confirm
 * mandatory 消息从交换机发送至队列结果的确认 实现原理: 基于ReturnListener PublisherCallbackChannelImpl
 * 参考rabbitmq-api d05_broker_mandatory
 *
 * @author sobann
 */
@Configuration
public class RabbitmqConfig implements RabbitListenerConfigurer {

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${spring.rabbitmq.host:localhost}") String host,
            @Value("${spring.rabbitmq.port:5672}") int port,
            @Value("${spring.rabbitmq.username:demo}") String username,
            @Value("${spring.rabbitmq.password:demo}") String password,
            @Value("${spring.rabbitmq.virtual-host:/}") String vhost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         RabbitTemplate.ReturnCallback returnCallback, RabbitTemplate.ConfirmCallback confirmCallback) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setReturnCallback(returnCallback);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        // 要想使 returnCallback 生效，必须设置为true
        rabbitTemplate.setMandatory(true);
        /*
            生产者消息序列化 使用json 需要ObjectMapper
            默认使用org.springframework.amqp.support.converter.SimpleMessageConverter
         */
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * 消息反序列化 使用json字符串
     * 默认用jdk的序列化方式 在发送消息和接收消息用相同的类即可
     *
     * @param rabbitListenerEndpointRegistrar
     */
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(new MappingJackson2MessageConverter());
        rabbitListenerEndpointRegistrar.setMessageHandlerMethodFactory(factory);
    }
}
