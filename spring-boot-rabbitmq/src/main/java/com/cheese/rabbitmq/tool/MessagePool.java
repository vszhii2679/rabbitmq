package com.cheese.rabbitmq.tool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放消息的池子，模拟上下游数据存储
 *
 * @author sobann
 */
public class MessagePool {

    public static final Map<String, String> UN_ACK_MESSAGE = new ConcurrentHashMap<>(64);


    public static void push(String key, String message) {
        UN_ACK_MESSAGE.put(key, message);
    }

    public static String remove(String key) {
        return UN_ACK_MESSAGE.remove(key);
    }

    public static String select(String key) {
        return UN_ACK_MESSAGE.get(key);
    }
}