package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.infrastructure.messaging.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/kafka")
public class KafkaController {
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/send")
    public String sendMessage(@RequestParam String key, @RequestBody Object message) {
        kafkaProducerService.sendMessage("my-topic", key, message);
        return "Message sent to Kafka: " + key + " : " + message;
    }
}
