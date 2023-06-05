package com.cheese.rabbitmq.d10_transaction;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * rabbitmq事务
 * 消费者
 * tip: 事务效率低下 实际项目中 使用publisher confirm来代替事务
 *
 * @author sobann
 */
public class TransactionConsumer extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(ConnectionSupport.QUEUE_NAME, false, false, false, null);
        channel.queueBind(ConnectionSupport.QUEUE_NAME, ConnectionSupport.EXCHANGE_NAME, ConnectionSupport.ROUTING_KEY);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };

        channel.basicConsume(ConnectionSupport.QUEUE_NAME, true, consumer);

        Thread.sleep(1000L);
        channel.close();
        connection.close();
    }
}
