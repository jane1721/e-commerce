package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.infrastructure.messaging.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private final KafkaProducer kafkaProducer;

    @Value("${e-commerce-api.test.topic-name}")
    String topicName;

    @PostMapping("/send")
    public String sendMessage(@RequestParam String key, @RequestBody Object message) {
        kafkaProducer.sendMessage(topicName, key, message);
        return "Message sent to Kafka: " + key + " : " + message;
    }
}
