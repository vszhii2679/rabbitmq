package com.cheese.rabbitmq.d01_quickstart;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 快速开始
 * mq连接会话提供者
 *
 * @author sobann
 */
public abstract class QuickStartConnectionSupport {
    public static final Logger log = LoggerFactory.getLogger(QuickStartConnectionSupport.class);
    public static final String EXCHANGE_NAME = "quickStart_exchange";
    public static final String ROUTING_KEY = "error";
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
}
