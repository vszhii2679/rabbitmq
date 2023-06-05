package com.cheese.rabbitmq.d10_transaction;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * rabbitmq事务
 * 生产者
 * tip: 事务效率低下 实际项目中 使用publisher confirm来代替事务
 *
 * @author sobann
 */
public class TransactionProducer extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明交换机
        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // channel开启事务
        channel.txSelect();
        for (int i = 0; i < 3; i++) {
            try {
                String message = "hello rabbitmq transaction " + i;
                channel.basicPublish(ConnectionSupport.EXCHANGE_NAME, ConnectionSupport.ROUTING_KEY, false, null, message.getBytes());
                log.info("send message, routingKey: {}, message: {}", ConnectionSupport.ROUTING_KEY, message);
                // 提交事务
                channel.txCommit();
            } catch (Exception e) {
                e.printStackTrace();
                // 回滚事务
                channel.txRollback();
            }

        }

        channel.close();
        connection.close();
    }
}
