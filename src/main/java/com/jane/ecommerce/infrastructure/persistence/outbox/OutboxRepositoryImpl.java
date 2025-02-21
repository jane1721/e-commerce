package com.jane.ecommerce.infrastructure.persistence.outbox;

import com.jane.ecommerce.domain.outbox.Outbox;
import com.jane.ecommerce.domain.outbox.OutboxEntity;
import com.jane.ecommerce.domain.outbox.OutboxRepository;
import com.jane.ecommerce.domain.outbox.OutboxStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    public Outbox save(Outbox outbox) {
        return outboxJpaRepository.save(OutboxEntity.toEntity(outbox)).toDomain();
    }

    @Override
    public Outbox findById(String id) {
        return outboxJpaRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new)
                .toDomain();
    }

    @Override
    public List<Outbox> findAllByStatusAndUpdatedAtBefore(OutboxStatus outboxStatus, LocalDateTime targetDatetime) {
        return outboxJpaRepository.findAllByStatusAndUpdatedAtBefore(outboxStatus, targetDatetime).stream()
                .map(OutboxEntity::toDomain)
                .toList();
    }
}
