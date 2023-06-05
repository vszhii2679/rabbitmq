package com.cheese.rabbitmq.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

/**
 * redis操作的工具类
 *
 * @author sobann
 */
public class RedisUtil {

    private static final StringRedisTemplate redisTemplate;
    private static final ObjectMapper objectMapper;

    private RedisUtil() {
    }

    static {
        redisTemplate = ApplicationContextHelper.getBean(StringRedisTemplate.class);
        objectMapper = new ObjectMapper();

    }

    public static void removeCache(String key) {
        redisTemplate.delete(key);
    }

    public static void createCache(String key, String cache) {
        redisTemplate.boundValueOps(key).set(cache);
    }


    private static String gainCache(String key) {
        return redisTemplate.boundValueOps(key).get();
    }

    public static <T> T get(String key, Class<T> clazz, Supplier<T> sup) {
        T t;
        try {
            String cache = gainCache(key);
            if (StringUtils.isEmpty(cache)) {
                t = sup.get();
                if (!clazz.isAssignableFrom(String.class)) {
                    cache = objectMapper.writeValueAsString(t);
                }
                createCache(key, cache);
            } else {
                t = objectMapper.readValue(cache, clazz);
            }
        } catch (Exception e) {
            throw new RuntimeException("json deal error");
        }
        return t;
    }
}
