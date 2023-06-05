package com.cheese.rabbitmq.d02_exchange.topic;

import com.cheese.rabbitmq.d02_exchange.ExchangeConnectionSupport;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * topic模式
 * 生产者
 * topic模式是direct模式的一种叠加，增加了模糊路由的模式
 *
 * # 可以匹配一个词或多个词
 * * 匹配一个词
 * info.# 可以匹配 info.aaa 和 info.aaa.bbb
 * info.* 可以匹配 info.aaa 但不能匹配 info.aaa.bbb
 *
 * @author sobann
 */
public class TopicProducer extends ExchangeConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明交换机 tip: 交换机、队列最好在rabbitmq中手动创建，这里声明作为缺省的写法
        channel.exchangeDeclare(ExchangeConnectionSupport.TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);


        for (int i = 0; i < 4; i++) {
            String routingKey = TOPIC_ROUTING_KEYS[i % 4];
            String message = "hello rabbitmq topic, routingKey is " + routingKey;
            channel.basicPublish(ExchangeConnectionSupport.TOPIC_EXCHANGE_NAME, routingKey, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", routingKey, message);
        }

        channel.close();
        connection.close();
    }
}
