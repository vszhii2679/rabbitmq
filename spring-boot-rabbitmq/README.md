[toc]

### spring-boot-rabbitmq


#### 代码结构
```markdown
rabbitmq
├── config -- rabbitTemplate配置、ConfirmCallback和ReturnCallback回调demo
├── modules
    ├── log -- 模拟监听日志消息的业务
└── RabbitApplication 消息发送测试
```

#### confirm mandatory、ack｜nack在spring中的实现
```markdown
# confirm回调 基于ConfirmListener 参考rabbitmq-api d04_broker_confirm
PublisherCallbackChannelImpl的handleAck和handleNack最终调用RabbitTemplate.ConfirmCallback.confirm
# mandatory回调 基于ReturnListener 参考rabbitmq-api d05_broker_mandatory
PublisherCallbackChannelImpl的handleReturn最终调用RabbitTemplate.ReturnCallback.returnedMessage

```

#### 注意事项
```markdown
# 使用手动签收时候需要配置 默认配置auto 否则会出现重复签收的错误
spring.rabbitmq.listener.simple.acknowledge-mode=manual

# 重复签收错误信息
Channel shutdown: channel error; protocol method: #method<channel.close>(reply-code=406, reply-text=PRECONDITION_FAILED - unknown delivery tag 1, class-id=60, method-id=80)

# 消息序列化
生产和消费消息的序列化和反序列化方式要对应 否则会出错
默认使用JDK的方式 org.springframework.amqp.support.converter.SimpleMessageConverter
序列化 SimpleMessageConverter.toMessage 将消息对象包装成Message对象
反序列化 SimpleMessageConverter.fromMessage 将Message对象解析为消息对象

# spring-rabbit的重试机制 核心参数以函数式接口内部类对象的方式传递
org.springframework.amqp.rabbit.core.ChannelCallback.doInRabbit
org.springframework.retry.RetryCallback.doWithRetry

通过org.springframework.retry.support.RetryTemplate.execute调用
当方法运行成功时，这两个内部类返回null，失败抛出异常

# rabbitmq消费顺序性问题
1.单个队列，单个消费者保证顺序 rabbitmq中队列本身就是有序的，单个消费者一定有序
2.单个队列，多个消费者 对于有顺序要求的业务消息，设计业务组
    在消息按照顺序发送的同时，在消息体中增加顺序标志，业务组标记，消费者在拿到消息时候 需要判断上一顺序位的消息是否完成，否则拒绝签收 + 重回队列
    这种情况下的消息失败重试机制

```