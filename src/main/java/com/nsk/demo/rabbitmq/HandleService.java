package com.nsk.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nsk
 * 2018/7/25 11:06
 */
@Service
public class HandleService implements ChannelAwareMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(HandleService.class);

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        byte[] body = message.getBody();
        String type = new String(body);
        System.out.println("接收到的消息：" + type);
        boolean isDeal = false;
        switch (type){
            case "type1":
                isDeal = type1(type);
                break;
            case "type2":
                isDeal = type2(type);
                break;
            case "type1--1":
                isDeal = type1(type);
                break;
        }
        if (isDeal) {
            basicAck(message, channel);
        } else {
            basicNACK(message, channel);
        }
    }

    private boolean type1(String type) throws Exception {
        logger.info("The business:" + type + " is dealing with");
        Thread.sleep(3000);
        logger.info("The business:" + type + " is resolved");
        return true;
    }

    private boolean type2(String type) throws Exception {
        logger.info("The business:" + type + " is dealing with");
        Thread.sleep(3000);
        logger.info("The business:" + type + " is resolved");
        return true;
    }

    /**
     * 消费者处理成功后，将任务移除
     *
     * @param message
     * @param channel
     */
    private void basicAck(Message message, Channel channel) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            logger.error("通知服务器移除mq时异常，异常信息：" + e);
        }
    }

    /**
     * 消费者处理失败后，处理
     *
     * @param message
     * @param channel
     */
    private void basicNACK(Message message, Channel channel) {
        try {
            // requeue为true时，重新进入队列； requeue为false时，不进入队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        } catch (IOException e) {
            logger.error("mq重新进入服务器时出现异常，异常信息：" + e);
        }
    }

}
