package com.cheese.rabbitmq.d05_broker_mandatory;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * broker Mandatory机制
 * 开启mandatory模式时，当rabbitmq broker接收到消息生产者发送的消息时后，消息在broker内部是从交换机路由到队列
 *      若交换机路由到队列失败，则会回调ReturnListener.handleReturn方法
 *
 * tip: Confirm机制使用监听器未ConfirmListener，Mandatory回调的监听器使用ReturnListener
 *
 * 实际生产中路由失败可能是因为mq内部的错误、磁盘写满、路由队列满造成的
 * 这里模拟路由失败的方式是通过不声明队列哦～
 *
 * @author sobann
 */
public class MandatoryProducer extends ConnectionSupport{

    public static void main(String[] args) throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 声明交换机，但是没有队列，模拟路由失败的场景
        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 添加监听器
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.error("routing message error, replyCode: {}, replyText: {}, exchange: {}, routingKey: {}",
                        replyCode, replyText, exchange, routingKey);
            }
        });

        String message = "hello rabbit mandatory ";
        // 确认发送的消息正确路由到队列中，需要配置mandatory为true服务器才会回调
        channel.basicPublish(ConnectionSupport.EXCHANGE_NAME, ConnectionSupport.ROUTING_KEY, true, null, message.getBytes());

        Thread.sleep(1000L);
        channel.close();
        connection.close();
    }
}
