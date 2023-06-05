package com.cheese.rabbitmq.modules.log.service;

/**
 * 业务逻辑
 *
 * @author sobann
 */
public interface ILogService {

    /**
     * 写入消息的json
     *
     * @param message
     */
    void writeLog(String message);
}
