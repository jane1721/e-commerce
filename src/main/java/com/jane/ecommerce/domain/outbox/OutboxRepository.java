package com.jane.ecommerce.domain.outbox;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository {

    Outbox save(Outbox outbox);

    Outbox findById(String id);

    List<Outbox> findAllByStatusAndUpdatedAtBefore(OutboxStatus outboxStatus, LocalDateTime targetDatetime);
}
