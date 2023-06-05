package com.cheese.rabbitmq.d02_exchange.topic;

import com.cheese.rabbitmq.d02_exchange.ExchangeConnectionSupport;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;

/**
 * topic模式
 * 消费者
 * routingKey只要符合#.login.#的匹配规则 数据会被推送到topic_login关联的队列中
 *
 * @author sobann
 */
public class TopicLoginConsumer extends ExchangeConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明交换机 tip: 交换机、队列最好在rabbitmq中手动创建，这里声明作为缺省的写法
        channel.exchangeDeclare(ExchangeConnectionSupport.TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        // 声明login队列
        channel.queueDeclare("topic_login", false, false, false, null);
        channel.queueBind("topic_login", ExchangeConnectionSupport.TOPIC_EXCHANGE_NAME, "#.login.#");

        // 消费者
        Consumer consumer = createConsumer(channel);
        // 消费
        channel.basicConsume("topic_login", true, consumer);

        Thread.sleep(1000L);
        channel.close();
        connection.close();
    }
}
