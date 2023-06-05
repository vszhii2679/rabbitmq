package com.cheese.rabbitmq.d02_exchange.fanout;

import com.cheese.rabbitmq.d02_exchange.ExchangeConnectionSupport;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * fanout模式/订阅广播模式
 * 生产者
 * 在fanout模式下，交换机将消息发送给绑定的所有订阅的消息队列
 *
 *
 * @author sobann
 */
public class FanoutProducer extends ExchangeConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明交换机 tip: 交换机、队列最好在rabbitmq中手动创建，这里声明作为缺省的写法
        channel.exchangeDeclare(ExchangeConnectionSupport.FANOUT_EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        for (int i = 0; i < 3; i++) {
            // 在fanout模式中队列与交换机绑定不需要routingKey
            String routingKey = "";
            String message = "hello rabbitmq fanout " + i;
            channel.basicPublish(ExchangeConnectionSupport.FANOUT_EXCHANGE_NAME, routingKey, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", routingKey, message);
        }

        channel.close();
        connection.close();
    }
}
