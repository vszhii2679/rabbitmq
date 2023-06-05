package com.cheese.rabbitmq.d11_qos;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 限流策略 生产者
 * 限流是在消费段开启qos策略
 *
 * @author sobann
 */
public class QosProducer extends ConnectionSupport{

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 用fanout
        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, null);

        for (int i = 0; i < 100; i++) {
            String message = "hello rabbitmq qos " + i;
            channel.basicPublish(ConnectionSupport.EXCHANGE_NAME, "qos.save",true, null, message.getBytes());
        }

        channel.close();
        connection.close();
    }
}
