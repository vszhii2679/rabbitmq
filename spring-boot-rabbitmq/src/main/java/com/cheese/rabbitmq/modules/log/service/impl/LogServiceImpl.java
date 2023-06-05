package com.cheese.rabbitmq.modules.log.service.impl;

import com.cheese.rabbitmq.modules.log.service.ILogService;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author sobann
 */
@Service("logService")
public class LogServiceImpl implements ILogService {

    @Override
    public void writeLog(String message) {
        try {
            // 模拟业务异常 nack
            if ((new Random().nextInt() & 1) == 1) {
//            if (true) {
                throw new RuntimeException("business error");
            }
            System.out.println("write log to elk success ~");
        } catch (Exception e) {
            System.out.println("write log to elk error ~");
            throw new RuntimeException(e.getMessage());
        }
    }
}
