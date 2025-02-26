package com.jane.ecommerce.domain.payment.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentOutboxService {

    private final PaymentOutboxRepository paymentOutboxRepository;

    @Transactional
    public PaymentOutbox save(PaymentOutbox outbox) {
        return paymentOutboxRepository.save(outbox);
    }

    public PaymentOutbox findById(String id) {
        return paymentOutboxRepository.findById(id);
    }

    public List<PaymentOutbox> findAllByStatusAndUpdatedAtBefore(PaymentOutboxStatus status, LocalDateTime targetDatetime) {
        return paymentOutboxRepository.findAllByStatusAndUpdatedAtBefore(status, targetDatetime);
    }
}
