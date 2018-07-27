package com.nsk.demo.rabbitmq;

import com.nsk.demo.rabbitmq.queue.QueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.common.AmqpServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author nsk
 * 2018/7/21 9:35
 */
@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class RabbitMQConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfiguration.class);

    @Autowired
    private ConnectionFactory connectionFactory;

    @PostConstruct
    public void init() {
        connectionFactory.addConnectionListener(new ConnectionListener() {
            @Override
            public void onCreate(Connection connection) {
                logger.info("The connection listener is connected!");
            }

            @Override
            public void onClose(Connection connection) {
                logger.info("The connection listener is closed!");
            }
        });
    }


    //监听处理类
    @Bean
    @Scope("prototype")
    public HandleService handleService() {
        return new HandleService();
    }

    @Autowired
    private QueueService queueService;

    //创建监听器，监听队列
    @Bean
    public SimpleMessageListenerContainer mqMessageContainer(HandleService handleService) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        List<String> queueNames = queueService.getQueueNames();
        container.setQueueNames(queueNames.toArray(new String[queueNames.size()]));
        container.setExposeListenerChannel(true);
        container.setPrefetchCount(100);//设置每个消费者获取的最大的消息数量
        container.setConcurrentConsumers(100);//消费者个数
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);//设置确认模式为手工确认
        container.setChannelAwareMessageListener(handleService);//监听处理类
        return container;
    }

    @Bean
    @Profile("nsk-rabbit")
    public ConnectionFactory connectionFactory(RabbitMQProperties config) {
        logger.info("connecting to rabbitmq service with name {}", config.getServiceName());
        CloudFactory cloudFactory = new CloudFactory();
        Cloud cloud = cloudFactory.getCloud();
        AmqpServiceInfo serviceInfo = (AmqpServiceInfo) cloud.getServiceInfo(config.getServiceName());
        String serverID = serviceInfo.getId();
        return cloud.getServiceConnector(serverID, ConnectionFactory.class, null);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, RabbitMQProperties config) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        // this interface is used to receive callback after message's sending to rabbitmq's exchange
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                logger.info("The message is send succeed!");
            } else {
                logger.info("The message is send failed!");
            }
        });
        rabbitTemplate.setReturnCallback((message, i, s, s1, s2) ->
                logger.info("The message is" + message.toString())
        );
        return rabbitTemplate;
    }

}
