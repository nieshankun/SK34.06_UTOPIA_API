package com.nsk.demo.rabbitmq;

import com.nsk.demo.rabbitmq.queue.QueueService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author nsk
 * 2018/7/21 9:35
 */
@Service
public class RabbitMQService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private QueueService queueService;

    @Autowired
    private AmqpAdmin rabbitAdmin;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private SimpleMessageListenerContainer simpleMessageListenerContainer;

    public void sendStatic(){
        rabbitTemplate.convertAndSend("nsk", "handsome", "type1");
        rabbitTemplate.convertAndSend("hsy", "first", "type2");
        rabbitTemplate.convertAndSend("nsk", "handsome", "type1");
    }

    public void sendDynamic(String queueName, String exchange, String routingKey) {
        dealt(queueName, exchange, routingKey);
        addBinding(queueName, exchange, routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, "Hello,world!");
    }

    /**
     * 将新队列添加到监听器中
     * @param queueName 队列名
     * @param exchange  交换路由
     * @param routingKey 访问键
     */
    private void dealt(String queueName, String exchange, String routingKey) {
        Connection con = connectionFactory.createConnection();
        Channel channel = null;
        try{
            channel = con.createChannel(false);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchange, routingKey);
        } catch (Exception e) {
            logger.error("Channel binding is wrong!");
        } finally {
            try{
                if (channel != null){
                    channel.close();
                }
            }catch (Exception e){
                logger.error("The error is produce when closed the channel !");
            }
            con.close();
        }
        List<String> queueNameList = queueService.getQueueNames();
        if (!queueNameList.contains(queueName)){
            queueService.saveQueue(queueName, exchange, routingKey);
        }
        simpleMessageListenerContainer.addQueueNames(queueName);
    }

    /**
     * 将队列、路由绑定到rabbitMQ服务
     * @param queueName 队列名
     * @param exchange 路由交换
     * @param routingKey 访问键
     */
    public void addBinding(String queueName, String exchange, String routingKey) {
        DirectExchange directExchange = new DirectExchange(exchange);
        Binding binding = BindingBuilder.bind(new Queue(queueName)).to(directExchange).with(routingKey);
        rabbitAdmin.declareExchange(new DirectExchange(exchange));
        rabbitAdmin.declareQueue(new Queue(queueName));
        rabbitAdmin.declareBinding(binding);
    }

}
