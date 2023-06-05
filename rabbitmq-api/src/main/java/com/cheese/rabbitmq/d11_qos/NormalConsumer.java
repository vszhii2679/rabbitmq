package com.cheese.rabbitmq.d11_qos;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 限流策略 消费者
 * 通过Channel.basicQos设置限流
 *
 * @author sobann
 */
public class NormalConsumer extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();


        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, null);
        channel.queueDeclare("qos_normal_queue", true, false, false, null);
        channel.queueBind("qos_normal_queue", ConnectionSupport.EXCHANGE_NAME, "qos.#");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws
                    IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };

        // 限流时必须手动签收
        channel.basicConsume("qos_normal_queue", true, consumer);

        Thread.sleep(3000L);
        channel.close();
        connection.close();
    }


}
