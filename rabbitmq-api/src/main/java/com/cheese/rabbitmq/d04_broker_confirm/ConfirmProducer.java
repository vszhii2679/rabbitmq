package com.cheese.rabbitmq.d04_broker_confirm;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * broker confirm机制
 * 当rabbitmq broker接收到消息生产者发送的消息时，会依据confirm方式进行处理
 *
 * 同步confirm 发布者发送消息后通过channel.waitForConfirms手动确认消息是否成功发送到broker
 *  此方法可以批量确认已发送未确认的消息
 *
 * 异步confirm 发布者注册confirm回调监听器ConfirmListener (较常用)
 *  当消息发送broker成功，回调ConfirmListener.handleAck
 *  当消息发送broker失败，回调ConfirmListener.handleNack
 *      失败的情况：磁盘无法写入、队列达到上限、mq异常
 *
 * @author sobann
 */
public class ConfirmProducer extends ConnectionSupport {

    public static void main(String[] args) throws Exception {
//        syncConfirm();
        asyncConfirm();
//        batchConfirm();
    }

    public static void syncConfirm() throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 交换机
        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 开启发布者confirm模式
        channel.confirmSelect();


        for (int i = 0; i < 5; i++) {
            String message = "hello rabbitmq broker sync confirm " + i;
            channel.basicPublish(ConnectionSupport.EXCHANGE_NAME, ConnectionSupport.ROUTING_KEY, null, message.getBytes());
            // 一条条确认，返回未true
            if (channel.waitForConfirms()) {
                log.info("send message, routingKey: {}, message: {}", ConnectionSupport.ROUTING_KEY, message);
            } else {
                log.info("send fail, routingKey: {}, message: {}", ConnectionSupport.ROUTING_KEY, message);
            }
        }

        channel.close();
        connection.close();
    }

    /**
     * 异步confirm
     */
    public static void asyncConfirm() throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 交换机
        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 开启confirm模式，保证消息推送到broker中 这里使用异步的confirm 通过监听器回调
        channel.confirmSelect();
        // 添加confirm回调监听器，测试broker是否接收到消息，若有多条消息未确认，rabbitmq会开启批量处理(根据deliveryTag)
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                log.info("broker accept message, deliveryTag: {}, multiple: {}", deliveryTag, multiple);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                log.info("broker can not accept message, deliveryTag: {}, multiple: {}", deliveryTag, multiple);
            }
        });

        for (int i = 0; i < 5; i++) {
            String message = "hello rabbitmq broker async confirm " + i;
            channel.basicPublish(ConnectionSupport.EXCHANGE_NAME, ConnectionSupport.ROUTING_KEY, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", ConnectionSupport.ROUTING_KEY, message);
        }

        Thread.sleep(1000L);
        channel.close();
        connection.close();
    }

    /**
     * 批量confirm 还是同步的
     */
    public static void batchConfirm() throws Exception {
        Connection connection = getConnection("localhost", 5672, "/");
        Channel channel = connection.createChannel();

        // 交换机
        channel.exchangeDeclare(ConnectionSupport.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 开启confirm模式，保证消息推送到broker中 这里使用异步的confirm 通过监听器回调
        channel.confirmSelect();


        for (int i = 0; i < 4; i++) {
            String message = "hello rabbitmq broker sync confirm " + i;
            channel.basicPublish(ConnectionSupport.EXCHANGE_NAME, ConnectionSupport.ROUTING_KEY, null, message.getBytes());
            log.info("send message, routingKey: {}, message: {}", ConnectionSupport.ROUTING_KEY, message);
            // 每两条消息确认一次
            if ((i & 1) == 1) {
                // 一条条确认，返回未true
                if (channel.waitForConfirms()) {
                    log.info("send batch success");
                } else {
                    log.error("send batch failed");

                }
            }
        }

        channel.close();
        connection.close();
    }
}
