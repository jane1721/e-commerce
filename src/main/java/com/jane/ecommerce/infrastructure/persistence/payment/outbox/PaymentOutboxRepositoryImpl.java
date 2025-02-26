package com.jane.ecommerce.infrastructure.persistence.payment.outbox;

import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.payment.outbox.PaymentOutbox;
import com.jane.ecommerce.domain.payment.outbox.PaymentOutboxEntity;
import com.jane.ecommerce.domain.payment.outbox.PaymentOutboxRepository;
import com.jane.ecommerce.domain.payment.outbox.PaymentOutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

    private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Override
    public PaymentOutbox save(PaymentOutbox outbox) {
        return paymentOutboxJpaRepository.save(PaymentOutboxEntity.from(outbox)).toDomain();
    }

    @Override
    public PaymentOutbox findById(String id) {
        return paymentOutboxJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ id }))
                .toDomain();
    }

    @Override
    public List<PaymentOutbox> findAllByStatusAndUpdatedAtBefore(PaymentOutboxStatus status, LocalDateTime targetDatetime) {
        return paymentOutboxJpaRepository.findAllByStatusAndUpdatedAtBefore(status, targetDatetime).stream()
                .map(PaymentOutboxEntity::toDomain)
                .toList();
    }
}
