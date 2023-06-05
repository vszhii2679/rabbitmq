package com.cheese.rabbitmq.d02_exchange.direct;

import com.cheese.rabbitmq.d02_exchange.ExchangeConnectionSupport;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * direct模式
 * 一个交换机向多个routing-key发送消息
 * 如果一个direct交换机绑定了多个队列，交换机会将消息分别路由给每一个队列，每一个队列都会得到一份全量的消息
 *
 * @author sobann
 */
public class DirectProducer extends ExchangeConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明交换机 tip: 交换机、队列最好在rabbitmq中手动创建，这里声明作为缺省的写法
        channel.exchangeDeclare(ExchangeConnectionSupport.DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 通过交换机向具体routing-key发送消息
        for (int i = 0; i < 3; i++) {
            String routingKey = DIRECT_ROUTING_KEYS[i % 3];
            String message =  "hello rabbitmq direct" + i;
            channel.basicPublish(ExchangeConnectionSupport.DIRECT_EXCHANGE_NAME, routingKey, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", routingKey, message);
        }

        channel.close();
        connection.close();
    }
}
