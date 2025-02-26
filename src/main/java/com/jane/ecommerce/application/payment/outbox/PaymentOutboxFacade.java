package com.jane.ecommerce.application.payment.outbox;

import com.jane.ecommerce.domain.payment.outbox.PaymentOutbox;
import com.jane.ecommerce.domain.payment.outbox.PaymentOutboxService;
import com.jane.ecommerce.domain.payment.outbox.PaymentOutboxStatus;
import com.jane.ecommerce.infrastructure.messaging.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PaymentOutboxFacade {

    private final PaymentOutboxService paymentOutboxService;
    private final KafkaProducer kafkaProducer;

    @Value("${e-commerce-api.payment.topic-name}")
    private String topicName;

    @Transactional
    public void retryOutboxMessage(LocalDateTime targetDatetime) {
        List<PaymentOutbox> outboxList = paymentOutboxService.findAllByStatusAndUpdatedAtBefore(PaymentOutboxStatus.INIT, targetDatetime);

        if (outboxList.isEmpty()) {
            return;
        }

        for (PaymentOutbox outbox : outboxList) {
            if (outbox.getCount() >= 3) {
                paymentOutboxService.save(outbox.failed());
                continue;
            }

            kafkaProducer.sendMessage(topicName, outbox.getId(), outbox.getMessage());
            paymentOutboxService.save(outbox.incrementCount());
        }
    }
}
