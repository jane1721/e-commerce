package com.jane.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class KafkaTest {
    private static final String TOPIC = "test";
    private static final AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private KafkaTemplate<String, Object> producer;

    @KafkaListener(topics = TOPIC)
    public void listen(String message) {
        counter.incrementAndGet(); // 리스너가 호출되면 1 증가
    }

    @Test
    void kafkaTest() {
        producer.send(TOPIC, "Hello, Jane!");
        await() // 검증될 때 까지 기다린다.
                .pollInterval(Duration.ofMillis(300)) // 300ms 마다 한 번씩
                .atMost(Duration.ofSeconds(2)) // 최대 2s 까지
                .untilAsserted(() -> { // 아래 모든 assertion 이 만족할 때 까지
                    System.out.println("counter.get() = " + counter.get());
                    assertThat(counter.get()).isEqualTo(1L);
                });
    }
}