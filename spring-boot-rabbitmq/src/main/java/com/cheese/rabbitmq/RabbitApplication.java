package com.cheese.rabbitmq;

import com.cheese.rabbitmq.tool.MessagePool;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.UUID;

/**
 * launcher 类
 *
 * @author sobann
 */
@SpringBootApplication
public class RabbitApplication implements CommandLineRunner, ApplicationContextAware {

    @Value("${log.info.queue:info.log.queue}")
    private String logQueue;
    @Value("${log.info.exchange:info.log.exchange}")
    private String logExchange;
    @Value("${log.info.key:info.log.key}")
    private String logRoutingKey;

    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        MessageProperties messageProperties = new MessageProperties();
        String id = UUID.randomUUID().toString();
        String msg = "rabbit nice work ~ ~ ~ ~ ~";
        messageProperties.setMessageId(id);
        messageProperties.setContentType("text/plain");
        messageProperties.setContentEncoding("utf-8");
        Message message = new Message(msg.getBytes(), messageProperties);


        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(id);
        // 发送一条正确路由的消息 测试ack+requeue
        rabbitTemplate.convertAndSend(logExchange, logRoutingKey, message, correlationData);

        // 路由一个没有队列绑定的routingKey测试mandatory
//        rabbitTemplate.convertAndSend(logExchange, "nonexistent_key", message, correlationData);
        MessagePool.push(id, msg);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
