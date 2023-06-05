package com.cheese.rabbitmq.d08_ae_exchange;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 交换机
 * mq连接会话提供者
 *
 * @author sobann
 */
public abstract class ConnectionSupport {
    public static final Logger log = LoggerFactory.getLogger(ConnectionSupport.class);
    public static final String ORIGIN_EXCHANGE_NAME = "origin_exchange";
    public static final String BACKUP_EXCHANGE_NAME = "backup_exchange";
    public static final String BACKUP_QUEUE_NAME = "backup_queue";
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
