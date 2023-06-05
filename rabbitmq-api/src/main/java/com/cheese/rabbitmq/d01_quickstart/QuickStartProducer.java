package com.cheese.rabbitmq.d01_quickstart;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.nio.charset.StandardCharsets;

/**
 * 快速开始
 * 消息生产者
 *
 * @author sobann
 */
public class QuickStartProducer extends QuickStartConnectionSupport {

    public static void main(String[] args) throws Exception {
        // 1.创建链接
        Connection connection = getConnection("localhost", 5672, "/");
        // 2.创建通道
        Channel channel = connection.createChannel();
        // 3.定义交换机 交换机类型使用direct 在direct模式下，消息会借由交换机发送至与routing-key匹配的队列
        channel.exchangeDeclare(QuickStartProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 4.通过通道发送消息
        for (int i = 0; i < 5; i++) {
            String message = "hello rabbitmq, the times is " + i;
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes(StandardCharsets.UTF_8) );
            log.info("send message, routingKey: {}, message: {}", ROUTING_KEY, message);
        }
        // 5.关闭相关资源
        channel.close();
        connection.close();
    }
}
