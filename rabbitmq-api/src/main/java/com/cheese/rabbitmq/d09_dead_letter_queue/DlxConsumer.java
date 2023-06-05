package com.cheese.rabbitmq.d09_dead_letter_queue;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 死信队列 消费者
 *
 * @author sobann
 */
public class DlxConsumer extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();


        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("dlx queue get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };

        channel.basicConsume("dlx_queue", true, consumer);

        Thread.sleep(1000L);
        channel.close();
        connection.close();
    }
}
