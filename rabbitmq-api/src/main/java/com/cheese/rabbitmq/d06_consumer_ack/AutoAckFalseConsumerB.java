package com.cheese.rabbitmq.d06_consumer_ack;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 消费者B消费签收
 *
 * @author sobann
 */
public class AutoAckFalseConsumerB extends ConnectionSupport {

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
                try {
                    /*
                        手动ack,确认后消息就会从mq中删除
                            deliveryTag 消息的唯一标识
                            multiple 是否批量确认deliveryTag编号之前未被确认的消息
                     */
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    /*
                        发生异常，发送拒绝nack，根据requeue参数判断消息是否重回队列
                            deliveryTag 消息唯一标识
                            multiple 是否批量拒绝deliveryTag编号之前未被确认的消息
                            requeue 是否重回队列
                     */
                    channel.basicNack(envelope.getDeliveryTag(), false, false);
                }
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };

        /*
           消费
            queue 队列名
            autoAck 自动确认消费
            consumer 消息消费者，需要实现回调功能
         */
        channel.basicConsume(ConnectionSupport.QUEUE_NAME, false, consumer);

        // 消费者B线程等待1s 消费方法调用在其他工作线程中被调用，将它打印出来！
        Thread.sleep(1000L);
        channel.close();
        connection.close();
    }
}
