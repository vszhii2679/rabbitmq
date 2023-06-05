package com.cheese.rabbitmq.d02_exchange.fanout;

import com.cheese.rabbitmq.d02_exchange.ExchangeConnectionSupport;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;

/**
 * fanout模式/订阅广播模式
 * 消费者A消费fanout模式下队列A中的内容
 *
 * 其实可以开启Runnable，这里用消费者对象的设计更方便理解
 * @author sobann
 */
public class FanoutConsumerB extends ExchangeConnectionSupport {

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明交换机  tip: 交换机、队列最好在rabbitmq中手动创建，这里声明作为缺省的写法
        channel.exchangeDeclare(ExchangeConnectionSupport.FANOUT_EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        // 声明队列
        channel.queueDeclare("fanoutQueueB", false, false, false, null);
        // 队列绑定交换机
        channel.queueBind("fanoutQueueB", ExchangeConnectionSupport.FANOUT_EXCHANGE_NAME, "");

        // 消费者
        Consumer consumer = createConsumer(channel);

        // 消费
        channel.basicConsume("fanoutQueueB", true, consumer);


        Thread.sleep(1000L);
        channel.close();
        connection.close();
    }
}
