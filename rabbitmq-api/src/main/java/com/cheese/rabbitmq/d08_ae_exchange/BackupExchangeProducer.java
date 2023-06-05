package com.cheese.rabbitmq.d08_ae_exchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * 备份交换机
 * 当消息无法从交换机路由到队列时(没有任意一个绑定队列的路由规则与消息匹配时)
 * 如果此交换机设置了备份交换机，这条消息就会呗转发到AE交换机属性指定的交换机进行消息路由
 *
 * 流程：
 *  1.声明备份交换机，声明主交换机，为主交换机设置备份交换机
 *  2.创建备份交换机绑定的队列（或者使用fanout模式）
 *  3.通过主交换机发送消息(需要主交换机的路由无法匹配到任意一个消息队列哦)
 *
 * @author sobann
 */
public class BackupExchangeProducer extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明备份交换机
        channel.exchangeDeclare(ConnectionSupport.BACKUP_EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        // 声明备份交换机关联的队列
        channel.queueDeclare(ConnectionSupport.BACKUP_QUEUE_NAME, false, false, false, null);
        // 备份交换机绑定队列 fanout模式不需要routingKey
        channel.queueBind(ConnectionSupport.BACKUP_QUEUE_NAME, ConnectionSupport.BACKUP_EXCHANGE_NAME, "");

        // 声明主交换机，并指定ae参数设置为备份交换机名称
        Map<String, Object> properties = new HashMap<>();
        properties.put("alternate-exchange", ConnectionSupport.BACKUP_EXCHANGE_NAME);
        channel.exchangeDeclare(ConnectionSupport.ORIGIN_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, properties);

        // 发送消息
        String message = "hello rabbit alternate-exchange";
        channel.basicPublish(ConnectionSupport.ORIGIN_EXCHANGE_NAME, "info", null, message.getBytes());
        log.info("send message, routingKey: {}, message: {}", "info", message);

        channel.close();
        connection.close();
    }
}
