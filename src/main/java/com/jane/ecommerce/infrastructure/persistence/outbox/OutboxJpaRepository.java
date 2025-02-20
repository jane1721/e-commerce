package com.jane.ecommerce.infrastructure.persistence.outbox;

import com.jane.ecommerce.domain.outbox.OutboxEntity;
import com.jane.ecommerce.domain.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity, String> {

    List<OutboxEntity> findAllByStatusAndUpdatedAtBefore(OutboxStatus status, LocalDateTime updatedAt);
}
