package com.cheese.rabbitmq.d07_nack_reject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;

/**
 * 消费者
 * 在这个demo中没什么用 简单清理一下RejectConsumer中重回队列的消息而已
 *
 * @author sobann
 */
public class NormalConsumer extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        Consumer consumer = new DefaultConsumer(channel);
        channel.basicConsume(ConnectionSupport.QUEUE_NAME, true, consumer);
        channel.close();
        connection.close();
    }
}
