package com.cheese.rabbitmq.d07_nack_reject;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 消息生产者 direct模式
 *
 * @author sobann
 */
public class MessageProducer extends ConnectionSupport{

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();
        // 交换机
        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        for (int i = 0; i < 5; i++) {
            String message = "hello rabbitmq nack or reject " + i;
            channel.basicPublish(ConnectionSupport.EXCHANGE_NAME, ConnectionSupport.ROUTING_KEY, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", ConnectionSupport.ROUTING_KEY ,message);
        }

        channel.close();
        connection.close();
    }
}
