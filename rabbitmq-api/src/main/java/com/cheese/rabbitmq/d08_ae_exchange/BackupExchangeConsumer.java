package com.cheese.rabbitmq.d08_ae_exchange;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 备份交换机
 * 消费者
 * 当主交换机通过routingKey无法匹配到任意一个队列时，主交换机会将信息转发到备份交换机中
 *
 * @author sobann
 */
public class BackupExchangeConsumer extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明备份交换机
        channel.exchangeDeclare(ConnectionSupport.BACKUP_EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        // 声明备份交换机关联的队列
        channel.queueDeclare(ConnectionSupport.BACKUP_QUEUE_NAME, false, false, false, null);
        // 备份交换机绑定队列 fanout模式不需要routingKey
        channel.queueBind(ConnectionSupport.BACKUP_QUEUE_NAME, ConnectionSupport.BACKUP_EXCHANGE_NAME, "");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };

        channel.basicConsume(ConnectionSupport.BACKUP_QUEUE_NAME, true, consumer);

        Thread.sleep(1000L);
        channel.close();
        connection.close();
    }
}
