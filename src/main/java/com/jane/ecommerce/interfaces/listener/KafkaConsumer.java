package com.jane.ecommerce.interfaces.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumer {
    @KafkaListener(topics = "my-topic", groupId = "${spring.application.name}")
    public void consumeMessage(ConsumerRecord<String, Object> record) {
        log.info("Received message: {} with key: {}", record.value(), record.key());
    }
}
