package com.nsk.demo.rabbitmq.queue;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author nsk
 * 2018/7/25 20:40
 */
@Data
@Entity
public class QueueInfo {

    @Id
    @GeneratedValue
    private Integer id;

    private String queueName;

    private String exchange;

    private String routingKey;

    private String status;

    private LocalDateTime createTime;

    public QueueInfo(){
        // for sonar
    }
}
