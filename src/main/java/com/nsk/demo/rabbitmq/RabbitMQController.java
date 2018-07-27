package com.nsk.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author nsk
 * 2018/7/21 10:12
 */
@RestController
public class RabbitMQController {

    @Autowired
    private RabbitMQService rabbitMQService;

    @GetMapping(path = "same")
    public String send(){
        rabbitMQService.sendStatic();
        return "Same succeed!";
    }

    @GetMapping(path = "producer/{newQueue}")
    public String producerBindQueue(@PathVariable String newQueue) {
        int index = newQueue.indexOf(".");
        String exchange = newQueue.substring(0, index);
        String routingKey = newQueue.substring(index+1);
        rabbitMQService.sendDynamic(newQueue, exchange, routingKey);
        return "different";
    }

}
