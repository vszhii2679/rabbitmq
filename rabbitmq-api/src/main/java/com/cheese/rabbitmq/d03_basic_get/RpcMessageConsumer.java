package com.cheese.rabbitmq.d03_basic_get;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;

/**
 * 消息消费者
 * 消费段持续
 *
 * @author sobann
 */
public class RpcMessageConsumer extends RpcWaitConnectionSupport{

    public static void run() throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();
        // 注册一个系统钩子释放资源
//        addReleaseShutdownHook(connection, channel);

        // 声明交换机 tip: 交换机、队列最好在rabbitmq中手动创建，这里声明作为缺省的写法
        channel.exchangeDeclare(RpcWaitConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 声明队列
        channel.queueDeclare(RpcWaitConnectionSupport.QUEUE_NAME, true, false, false, null);
        channel.queueBind(RpcWaitConnectionSupport.QUEUE_NAME, RpcWaitConnectionSupport.EXCHANGE_NAME, RpcWaitConnectionSupport.ROUTING_KEY);

        while (true) {
            /**
             * queue: 队列名
             * autoAck: 自动确认
             */
            GetResponse response = channel.basicGet(RpcWaitConnectionSupport.QUEUE_NAME, true);
            if (null != response) {
                log.info("get message, routingKey: {}, message: {}", response.getEnvelope().getRoutingKey(), new String(response.getBody()));
            }
        }
    }

}
