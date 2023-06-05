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
public class QosConsumer extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();


        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, null);
        channel.queueDeclare("qos_queue", true, false, false, null);
        channel.queueBind("qos_queue", ConnectionSupport.EXCHANGE_NAME, "qos.#");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws
                    IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        /*
            限流配置
                prefetchSize 消息本身的大小 如果设置为0  那么表示对消息本身的大小不限制
                prefetchCount 告诉rabbitmq不要一次性给消费者推送大于N个消息
                global 是否将上面的设置应用于整个通道，false表示只应用于当前消费者

            prefetchCount的配置要综合考虑网络传输时间、客户端消费消息业务耗时、网络状况
         */
        channel.basicQos(0, 1, false);
        // 限流时必须手动签收
        channel.basicConsume("qos_queue", false, consumer);

        Thread.sleep(3000L);
        channel.close();
        connection.close();
    }


}
