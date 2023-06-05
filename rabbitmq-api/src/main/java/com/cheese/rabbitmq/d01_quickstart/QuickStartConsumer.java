package com.cheese.rabbitmq.d01_quickstart;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 快速开始
 * 消息生产者
 *
 * @author sobann
 */
public class QuickStartConsumer extends QuickStartConnectionSupport {

    public static void main(String[] args) throws Exception {
        // 1.创建链接
        Connection connection = getConnection("localhost", 5672, "/");
        // 2.创建通道
        Channel channel = connection.createChannel();
        // 3.定义交换机 交换机类型使用direct 在direct模式下，消息会借由交换机发送至与routing-key匹配的队列
        channel.exchangeDeclare(QuickStartConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        /**
         * 4.定义队列
         * queue: 队列名
         * durable: 是否持久化
         * exclusive: 是否独占
         * autoDelete: 队列脱离exchange，自动删除
         * arguments: 扩展参数
         */
        String queueName = "quickStartErrorQueue";
        channel.queueDeclare(queueName, true, false, false, null);
        /**
         * 5.绑定交换机和队列，通过router-key
         * queue: 队列名
         * exchange: 交换机名
         * routingKey: 路由key
         * arguments: 扩展参数
         */
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
        // 6.创建消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };
        /*
            7.消费者消费 异步消费 通过线程池执行任务->ConsumerWorkService$WorkPoolRunnable完成
            queue 队列名
            autoAck 自动确认消费
            consumer 消息消费者，需要实现回调功能
         */
        channel.basicConsume(queueName, true, consumer);

        // 主线程等待一下
        Thread.sleep(1000L);
        // 8.关闭相关资源
        channel.close();
        connection.close();
    }
}
