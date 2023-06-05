package com.cheese.rabbitmq;

import com.rabbitmq.client.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author sobann
 */
public class SocketChannelTest {
    private static final Logger log = LoggerFactory.getLogger(SocketChannelTest.class);

    public static ConnectionFactory connectionFactory = new ConnectionFactory();

    static {
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
    }

    @Test
    public void createChannel() throws Exception {
        // SocketFrameHandlerFactory
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("get message, routingKey: {}, message: {}", envelope.getRoutingKey(), message);
            }
        };
        while (true) {
            channel.basicConsume("quickStartErrorQueue", true, consumer);
        }
//        connection.close();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        // Consumer-Tag amq.ctag-cjHYEhasnH6X3rJkWWE4Ng
//        byte[] bytes = {0, 60, 0, 21, 31, 97, 109, 113, 46, 99, 116, 97, 103, 45, 80, 50, 55, 66, 110, 90, 71, 101, 83, 51, 108, 105, 77, 86, 86, 103, 77, 79, 81, 72, 56, 103};
        // Consumer-Tag amq.ctag-cjHYEhasnH6X3rJkWWE4Ng
//        byte[] bytes = {0, 60, 0, 21, 31, 97, 109, 113, 46, 99, 116, 97, 103, 45, 99, 106, 72, 89, 69, 104, 97, 115, 110, 72, 54, 88, 51, 114, 74, 107, 87, 87, 69, 52, 78, 103};
        // payload
//        byte[] bytes = {116, 101, 115, 116, 32, 115, 111, 99, 107, 101, 116, 32, 109, 101, 115, 115, 97, 103, 101, 50};
        byte[] bytes = {-26, -120, -111, -26, -104, -81, -28, -72, -128, -26, -82, -75, -26, -74, -120, -26, -127, -81};

        String s = new String(bytes);
        System.out.println("s = " + s);
    }
}
