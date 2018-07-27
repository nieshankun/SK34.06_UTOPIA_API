package com.nsk.demo.rabbitmq.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nsk
 * 2018/7/25 20:47
 */
@Service
public class QueueService {

    @Autowired
    private QueueRepository queueRepository;

    public List<String> getQueueNames() {
        List<String> queueNames = new ArrayList<>();
        List<QueueInfo> queueInfoList = queueRepository.getAllByStatus("VALID");
        queueInfoList.forEach(queueInfo -> queueNames.add(queueInfo.getQueueName()));
        return queueNames;
    }

    public void saveQueue(String queueName, String exchange, String routingKey) {
        QueueInfo queueInfo = new QueueInfo();
        queueInfo.setQueueName(queueName);
        queueInfo.setExchange(exchange);
        queueInfo.setRoutingKey(routingKey);
        queueInfo.setCreateTime(LocalDateTime.now());
        queueInfo.setStatus("VALID");
        queueRepository.save(queueInfo);
    }
}
