package com.cheese.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息是否从交换机发送至队列的回调
 *
 * @author sobann
 */
@Component
public class ReturnCallback implements RabbitTemplate.ReturnCallback{

    private static final Logger log = LoggerFactory.getLogger(ReturnCallback.class);

    /**
     * ReturnListener PublisherCallbackChannelImpl的handleReturn方法最终调用returnedMessage
     * ReturnListener.handleReturn方法被触发意味着消息从交换机路由到队列失败
     *
     * 可能的原因：
     *  1.没有路由匹配的队列 (测试一下存在备份交换机时候是否会调用这里)
     *  2.磁盘写满
     *  3.mq错误
     *
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        String msg = new String(message.getBody());
        log.error("message routing error, message: {} ,routingKey: {}", msg, routingKey);
        // todo 记录日志、发送邮件通知、定时扫描重发(需要入库)
    }
}
