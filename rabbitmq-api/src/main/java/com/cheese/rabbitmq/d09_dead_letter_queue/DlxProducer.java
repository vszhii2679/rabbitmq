package com.cheese.rabbitmq.d09_dead_letter_queue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列
 * 生产者
 * 消息成为死信的条件：
 * 1.消息被Channel.basicNack或者Channel.basicReject响应 且requeue为false(消息被拒绝且不重回队列)
 * 2.消息在队列存活时间超过TTL
 * 3.队列中的消息数量超过了最大队列长度
 *
 * 配置方法，正常业务交换机绑定的业务队列绑定死信交换机
 * tip:
 *  死信交换机和死信队列就是正常的交换机和队列(但只是处理以上三中情况出现的消息)
 *  可以利用死信条件中的TTL来处理定时业务
 *
 * @author sobann
 */
public class DlxProducer extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 死信队列绑定死信交换机
        channel.exchangeDeclare(ConnectionSupport.DLX_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, null);
        channel.queueDeclare("dlx_queue", true, false, false, null);
        channel.queueBind("dlx_queue", ConnectionSupport.DLX_EXCHANGE_NAME, "dlx.info");

        // 声明业务交换机和业务队列
        channel.exchangeDeclare(ConnectionSupport.NORMAL_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, null);

        // 业务队列绑定死信交换机
        Map<String, Object> properties = new HashMap<>(8);
        properties.put("x-dead-letter-exchange", ConnectionSupport.DLX_EXCHANGE_NAME);
        properties.put("x-dead-letter-routing-key", "dlx.info");

        channel.queueDeclare("normal_queue", true, false, false, properties);
        channel.queueBind("normal_queue", ConnectionSupport.NORMAL_EXCHANGE_NAME, "normal.info");


        // 通过业务交换机发送消息
        for (int i = 0; i < 3; i++) {
            // 设置消息的有效期
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                    // 1 非持久化 2持久化
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    // TTL
                    .expiration("10000")
                    .build();
            String message = "hello rabbitmq dead_letter " + i;
            channel.basicPublish(ConnectionSupport.NORMAL_EXCHANGE_NAME, "normal.info", true, basicProperties, message.getBytes());
        }

        channel.close();
        connection.close();
    }
}
