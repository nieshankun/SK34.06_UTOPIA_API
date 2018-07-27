package com.nsk.demo.rabbitmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author nsk
 * 2018/7/21 9:33
 */
@Data
@ConfigurationProperties("utopia.rabbitmq")
public class RabbitMQProperties {

    private String serviceName;

}
