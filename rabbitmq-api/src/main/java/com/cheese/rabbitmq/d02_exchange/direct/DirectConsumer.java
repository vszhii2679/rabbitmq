package com.cheese.rabbitmq.d02_exchange.direct;

import com.cheese.rabbitmq.d02_exchange.ExchangeConnectionSupport;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;

/**
 * direct模式
 * 消费者从routing-key绑定的队列中获取消息
 * routing-key和队列是一对一的关系
 *
 * @author sobann
 */
public class DirectConsumer extends ExchangeConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明交换机 tip: 交换机、队列最好在rabbitmq中手动创建，这里声明作为缺省的写法
        channel.exchangeDeclare(ExchangeConnectionSupport.DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        /*
            创建队列
              queue: 队列名
              durable: 是否持久化
              exclusive: 是否独占
              autoDelete: 队列脱离exchange，自动删除
              arguments: 扩展参数
            因为这里的消费者只消费routingKey为error绑定队列的消息，这里不对消息进行持久化
         */
        channel.queueDeclare("directExchangeErrorQueue", false, false, false, null);
        /*
            绑定队列、交换机和routingKey
            queue: 队列名称
            exchange: 交换机名称
            routingKey: 路由key
         */
        channel.queueBind("directExchangeErrorQueue", ExchangeConnectionSupport.DIRECT_EXCHANGE_NAME, "error");


        // 消费者
        Consumer consumer = createConsumer(channel);

        /*
            消费者消费 异步消费 通过线程池执行任务->ConsumerWorkService$WorkPoolRunnable完成
            queue 队列名
            autoAck 自动确认消费
            consumer 消息消费者，需要实现回调功能
         */
        channel.basicConsume("directExchangeErrorQueue", true, consumer);

        Thread.sleep(1000L);
        channel.close();
        connection.close();
    }
}
