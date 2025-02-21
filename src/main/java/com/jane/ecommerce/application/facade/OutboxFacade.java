package com.jane.ecommerce.application.facade;

import com.jane.ecommerce.domain.outbox.Outbox;
import com.jane.ecommerce.domain.outbox.OutboxService;
import com.jane.ecommerce.domain.outbox.OutboxStatus;
import com.jane.ecommerce.infrastructure.messaging.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxFacade {

    private final OutboxService outboxService;
    private final KafkaProducerService kafkaProducerService;

    @Value("${e-commerce-api.payment.topic-name}")
    private String topicName;

    @Transactional
    public void retryOutboxMessage(LocalDateTime targetDatetime) {
        List<Outbox> outboxList = outboxService.findAllByStatusAndUpdatedAtBefore(OutboxStatus.INIT, targetDatetime);

        if (outboxList.isEmpty()) {
            return;
        }

        for (Outbox outbox : outboxList) {
            if (outbox.getCnt() >= 3) {
                outboxService.save(outbox.failed());
                continue;
            }

            kafkaProducerService.sendMessage(topicName, outbox.getId(), outbox.getMessage());
            outboxService.save(outbox.incrementCnt());
        }
    }
}
