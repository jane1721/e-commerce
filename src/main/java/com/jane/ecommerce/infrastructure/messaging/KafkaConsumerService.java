package com.jane.ecommerce.infrastructure.messaging;

import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderClient;
import com.jane.ecommerce.domain.outbox.Outbox;
import com.jane.ecommerce.domain.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumerService {

    private final OutboxService outboxService;
    private final OrderClient orderClient;

    @Value("${e-commerce-api.payment.topic-name}")
    private String topicName;

    @KafkaListener(topics = "my-topic", groupId = "${spring.application.name}")
    public void consumeMessage(ConsumerRecord<String, Object> record) {
        log.info("Received message: {} with key: {}", record.value(), record.key());
    }

    // 1. 셀프 컨슈밍하는 리스너
    @KafkaListener(topics = "local.commerce.payment-process-event.v1", groupId = "payment-outbox")
    public void checkOutbox(ConsumerRecord<String, Object> record) {
        log.info("[payment-outbox] Received message: {} with key: {}", record.value(), record.key());

        // 아웃박스 데이터 읽으면서 상태 변경 update
        Outbox outbox = outboxService.findById(record.key());
        outboxService.save(outbox.published());
    }

    // 2. 비즈니스 로직 하는 리스너
    @KafkaListener(topics = "local.commerce.payment-process-event.v1", groupId = "send-process")
    public void reserved(ConsumerRecord<String, Object> record) throws InterruptedException {
        log.info("[send-process] Received message: {} with key: {}", record.value(), record.key());
        Order order = (Order) record.value();
        orderClient.send(order);

        // 주문 정보를 외부 데이터 플랫폼으로 전송
        boolean isSent = orderClient.send(order);

        if (!isSent) {
            throw new CustomException(ErrorCode.CONFLICT);
        }
    }

}
