package com.cheese.rabbitmq.modules.log.listener;

import com.cheese.rabbitmq.modules.log.service.ILogService;
import com.cheese.rabbitmq.tool.RedisUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 日志监听器
 * 使用ack完成消息消费的业务
 *
 * @author sobann
 */
@Component
public class LogListener {

    private static final Logger log = LoggerFactory.getLogger(LogListener.class);

    @Value("${log.info.queue:info.log.queue}")
    private String logQueue;
    @Value("${log.info.exchange:info.log.exchange}")
    private String logExchange;
    @Value("${log.info.key:info.log.key}}")
    private String logRoutingKey;
    @Value("${max.retry.count:3}")
    private int maxRetryCount;
    @Autowired
    private ILogService logService;

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            // 队列
                            value = @Queue(name = "${log.info.queue:info.log.queue}", durable = "true", exclusive = "false", autoDelete = "false"),
                            // 交换机
                            exchange = @Exchange(name = "${log.info.exchange:info.log.exchange}", type = ExchangeTypes.DIRECT),
                            // routing-key
                            key = "${log.info.key:info.log.key}"
                    )}

    )
    public void listenLog(Message message, Channel channel) throws Exception {
        channel.basicQos(0, 1, false);
        log.info("rabbit listener accept message from queue :{} , exchange: {}", logQueue, logExchange);
        /*
            ConfirmCallback保证消息投递到broker
            ReturnCallback保证消息投递到队列
            Ack/Nack 保证消息被正常消费
         */
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        // 消息唯一id
        String messageId = message.getMessageProperties().getMessageId();
        // 消息消费的幂等性
        Boolean hasConsume = RedisUtil.get(messageId + "::result", Boolean.class, () -> Boolean.FALSE);
        if (hasConsume) {
            log.info("the message has already consumed, where message id is {}, message is {}", messageId, new String(message.getBody()));
            return;
        }
        try {
            // 模拟业务
            logService.writeLog(msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            // 删除消息消费次数
            RedisUtil.removeCache(messageId + "::count");
            // 消息消费成功
            RedisUtil.createCache(messageId + "::result", String.valueOf(Boolean.TRUE));
        } catch (Exception e) {
            /*
                业务处理失败 nack 重回队列
                这里要注意的是如果是代码的问题，消费消息失败重回队列失败过程会一直重复，如果处理不及时会让系统的错误日志变得很大哦～
                一些非代码的错误，比如说序列化的问题，生产者和消费者对消息的序列化配置没有统一
                消息的错误要及时处理!!!
                消息的错误要及时处理!!!
                消息的错误要及时处理!!!

                消费业务处理失败消息重回队列会被推送到消息队列顶端进行 重复消费、失败重回队列的死循环
                在这里将失败的消息存入redis中并记录失败次数，如果超过重试次数，记录失败消息并丢弃消息
             */
            // 当前消息id唯一、redis处理线程安全，没必要使用AtomicInteger
            Integer messageRetryCount = RedisUtil.get(messageId + "::count", Integer.class, () -> 0);
            if (messageRetryCount < maxRetryCount) {
                // 可以重试 重试次数+1
                RedisUtil.createCache(messageId + "::count", String.valueOf(++messageRetryCount));
                // 消息消费结果
                RedisUtil.createCache(messageId + "::result", String.valueOf(Boolean.FALSE));
                // 重回队列
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } else {
                // todo 将消息内容持久化 消息进行IO会降低高并发下效率
                // 超过重试次数，丢弃消息 删除缓存
                RedisUtil.removeCache(messageId + "::count");
                RedisUtil.removeCache(messageId + "::result");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }
    }


}
