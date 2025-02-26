package com.jane.ecommerce.interfaces.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jane.ecommerce.application.payment.PaymentProcessedEvent;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.payment.outbox.PaymentOutbox;
import com.jane.ecommerce.domain.payment.outbox.PaymentOutboxService;
import com.jane.ecommerce.infrastructure.messaging.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class PaymentProcessedListener {

    private final PaymentOutboxService paymentOutboxService;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${e-commerce-api.payment.topic-name}")
    private String topicName;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(PaymentProcessedEvent event) throws JsonProcessingException {
        Order order = event.getOrder();

        // 아웃박스 엔티티 에다가 이벤트 정보를 저장한다.
        paymentOutboxService.save(PaymentOutbox.init(objectMapper.writeValueAsString(order)));
    }

    @Async // 비동기 처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 트랜잭션 커밋 후에 실행
    public void producePaymentProcessMessage(PaymentProcessedEvent event) {
        Order order = event.getOrder();

        // 이벤트를 produce 한다.
        kafkaProducer.sendMessage(topicName, order.getId().toString(), order);
    }
}
