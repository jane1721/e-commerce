package com.jane.ecommerce.domain.payment.outbox;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentOutboxRepository {

    PaymentOutbox save(PaymentOutbox outbox);

    PaymentOutbox findById(String id);

    List<PaymentOutbox> findAllByStatusAndUpdatedAtBefore(PaymentOutboxStatus status, LocalDateTime targetDatetime);
}
