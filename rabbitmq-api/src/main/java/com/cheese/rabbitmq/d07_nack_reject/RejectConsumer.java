package com.cheese.rabbitmq.d07_nack_reject;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 消息消费者
 * 消费时nack和reject都可以拒绝签收消息 nack可以批量处理
 *
 * @author sobann
 */
public class RejectConsumer extends ConnectionSupport{

    public static void main(String[] args) throws Exception {

        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();
        // 交换机
        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(ConnectionSupport.QUEUE_NAME, false, false, false, null);
        channel.queueBind(ConnectionSupport.QUEUE_NAME, ConnectionSupport.EXCHANGE_NAME, ConnectionSupport.ROUTING_KEY);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    String message = new String(body, StandardCharsets.UTF_8);
                    throw new RuntimeException("consume message error ,the routing-key is " + envelope.getRoutingKey() +" message is " + message);
                }catch (Exception e) {
                    e.printStackTrace();
                    /*
                        basicNack只比basicReject多一个是否批量的参数
                            deliveryTag
                            requeue
                     */
//                    channel.basicNack(envelope.getDeliveryTag(), false, true);
                    channel.basicReject(envelope.getDeliveryTag(), true);
                }

            }
        };

        // 消费
        channel.basicConsume(ConnectionSupport.QUEUE_NAME, false, consumer);

        Thread.sleep(1000L);
        channel.close();
        connection.close();
    }


}
