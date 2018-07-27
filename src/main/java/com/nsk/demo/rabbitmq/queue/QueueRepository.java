package com.nsk.demo.rabbitmq.queue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author nsk
 * 2018/7/25 20:48
 */
public interface QueueRepository extends JpaRepository<QueueInfo, Integer> {
    List<QueueInfo> getAllByStatus(String status);
}
