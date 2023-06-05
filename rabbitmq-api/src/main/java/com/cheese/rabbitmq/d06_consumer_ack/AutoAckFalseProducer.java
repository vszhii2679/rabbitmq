package com.cheese.rabbitmq.d06_consumer_ack;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 消息生产者
 * 当队列中的消息发送至消费者时，消费者不对消息进行确认 消息会一直保留在队列中 until消费者确认后才会删除
 * 消费者与rabbit连接中断，rabbit会考虑重投消息给另一个消费者
 *
 * 模拟这个过程 使用direct模式
 * 1.启动生产者AutoAckFalseProducer(简称P)、消费者AutoAckFalseConsumerA (简称A)
 * 2.P发送消息到rabbit交换机，rabbitmq通过交换机direct_exchange将消息路由(error)到消费者同时消费的队列directExchangeErrorQueue中
 * 3.A接收到队列中的消息但是不确认
 * 4.关闭A，启动生产者AutoAckFalseConsumerB(简称B)
 * 5.观察B接收的消息和A未签收的消息
 *
 * @author sobann
 */
public class AutoAckFalseProducer extends ConnectionSupport {
    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();
        // 交换机
        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        for (int i = 0; i < 5; i++) {
            String message = "hello rabbitmq ack " + i;
            channel.basicPublish(ConnectionSupport.EXCHANGE_NAME, ConnectionSupport.ROUTING_KEY, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", ConnectionSupport.ROUTING_KEY ,message);
        }
        channel.close();
        connection.close();
    }
}
