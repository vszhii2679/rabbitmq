package com.cheese.rabbitmq.d03_basic_get;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 消息生产者
 *
 * @author sobann
 */
public class RpcMessageProducer extends RpcWaitConnectionSupport {

    public static void run() throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明交换机 tip: 交换机、队列最好在rabbitmq中手动创建，这里声明作为缺省的写法
        channel.exchangeDeclare(RpcWaitConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        for (int i = 0; i < 4; i++) {
            String message = "hello rabbitmq long link " + i;
            channel.basicPublish(RpcWaitConnectionSupport.EXCHANGE_NAME, RpcWaitConnectionSupport.ROUTING_KEY, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", RpcWaitConnectionSupport.ROUTING_KEY, message);
            // 模拟1.5秒推送一条消息
            Thread.sleep(1500L);
        }

        channel.close();
        connection.close();
    }
}
