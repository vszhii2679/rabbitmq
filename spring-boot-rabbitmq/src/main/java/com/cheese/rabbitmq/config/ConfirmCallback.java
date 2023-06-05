package com.cheese.rabbitmq.config;

import com.cheese.rabbitmq.tool.MessagePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * confirm
 * 消息是否发送到broker中的回调
 * 基于rabbitmq-client
 * handleAck
 *
 * @author sobann
 */
@Component
public class ConfirmCallback implements RabbitTemplate.ConfirmCallback {

    private static final Logger log = LoggerFactory.getLogger(ConfirmCallback.class);

    /**
     * PublisherCallbackChannelImpl的handleAck和handleNack最终调用了RabbitTemplate.ConfirmCallback.confirm方法
     * 完成confirm的处理
     *
     *
     * @param correlationData 消息元数据
     * @param ack 消息投递至broker的结果
     * @param cause 错误信息
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        assert correlationData != null;
        String messageId = correlationData.getId();
        if (ack) {
            // 消息成功被broker接收，从未确认的消息池中移除此条消息
            String message = MessagePool.remove(messageId);
            log.info("send to broker success ,which id: {}, message: {}", messageId, message);
        } else {
            // 投递至broker失败，保存消息 todo 记录日志、发送邮件通知、定时扫描重发(需要入库)
            String message = MessagePool.select(messageId);
            log.error("send to broker failed ,which id: {}, message: {}, save message to artificial database ", messageId, message);
        }
    }
}
