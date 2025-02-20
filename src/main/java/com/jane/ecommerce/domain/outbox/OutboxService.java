package com.jane.ecommerce.domain.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OutboxService {

    private final OutboxRepository outboxRepository;

    @Transactional
    public Outbox save(Outbox outbox) {
        return outboxRepository.save(outbox);
    }

    public Outbox findById(String id) {
        return outboxRepository.findById(id);
    }

    public List<Outbox> findAllByStatusAndUpdatedAtBefore(OutboxStatus outboxStatus, LocalDateTime targetDatetime) {
        return outboxRepository.findAllByStatusAndUpdatedAtBefore(outboxStatus, targetDatetime);
    }
}
