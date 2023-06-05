package com.cheese.rabbitmq.d03_basic_get;

/**
 * 程序入口
 *
 * @author sobann
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // 先启动消费者
        new Thread(()-> {
            try {
                RpcMessageConsumer.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 再启动生产者
        RpcMessageProducer.run();
    }
}
