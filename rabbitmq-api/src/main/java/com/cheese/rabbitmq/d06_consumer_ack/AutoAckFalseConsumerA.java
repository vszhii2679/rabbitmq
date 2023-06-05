package com.cheese.rabbitmq.d06_consumer_ack;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 消费者A消费但不确认
 *
 * @author sobann
 */
public class AutoAckFalseConsumerA extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // declare和bind
        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(ConnectionSupport.QUEUE_NAME, false, false, false, null);
        channel.queueBind(ConnectionSupport.QUEUE_NAME, ConnectionSupport.EXCHANGE_NAME, ConnectionSupport.ROUTING_KEY);

        // 消费但不确认
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };

        /*
           消费
            queue 队列名
            autoAck 自动确认消费
            consumer 消息消费者，需要实现回调功能
         */

        // 消费者A线程等待5s后模拟宕机，连接超时关闭，在这个时候关闭A启动B哦
        while (true) {
            channel.basicConsume(ConnectionSupport.QUEUE_NAME, false, consumer);
        }
    }
}
