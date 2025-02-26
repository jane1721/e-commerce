package com.jane.ecommerce.infrastructure.persistence.payment.outbox;

import com.jane.ecommerce.domain.payment.outbox.PaymentOutboxEntity;
import com.jane.ecommerce.domain.payment.outbox.PaymentOutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutboxEntity, String> {

    List<PaymentOutboxEntity> findAllByStatusAndUpdatedAtBefore(PaymentOutboxStatus status, LocalDateTime updatedAt);
}
