package com.cheese.rabbitmq.d02_exchange;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 交换机
 * direct 通过router-key绑定队列直发
 * fanout 订阅广播，凡是订阅都可以消费
 * topic 话题匹配
 * mq连接会话提供者
 *
 * @author sobann
 */
public abstract class ExchangeConnectionSupport {
    public static final Logger log = LoggerFactory.getLogger(ExchangeConnectionSupport.class);
    public static final String DIRECT_EXCHANGE_NAME = "direct_exchange";
    public static final String FANOUT_EXCHANGE_NAME = "fanout_exchange";
    public static final String TOPIC_EXCHANGE_NAME = "topic_exchange";
    public static final String[] DIRECT_ROUTING_KEYS = {"info", "warning", "error"};
    public static final String[] TOPIC_ROUTING_KEYS = {"info.driver.A", "info.other", "warning.login.B", "error.crm.C"};
    public static ConnectionFactory connectionFactory = new ConnectionFactory();
    public static boolean init = false;

    public static void initConnectionFactory(String host, int port, String virtualHost) throws IOException, TimeoutException {
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setVirtualHost(virtualHost);
    }

    public static Connection getConnection(String host, int port, String virtualHost) throws IOException, TimeoutException {
        if (!init) {
            initConnectionFactory(host, port, virtualHost);
            init = true;
        }
        return connectionFactory.newConnection();
    }

    public static Consumer createConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };
    }
}
