package com.jane.ecommerce.domain.outbox;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Outbox {
    private final String id;
    private final String message;
    private OutboxStatus status;
    private int cnt;

    public Outbox(
            String id,
            String message,
            OutboxStatus status,
            int cnt
    ) {
        this.id = id;
        this.message = message;
        this.status = status;
        this.cnt = cnt;
    }

    public static Outbox init(String message) {
        return Outbox.builder()
                .id(UUID.randomUUID().toString())
                .message(message)
                .status(OutboxStatus.INIT)
                .cnt(0)
                .build();
    }

    public Outbox published() {
        this.status = OutboxStatus.PUBLISHED;
        return this;
    }

    public Outbox failed() {
        this.status = OutboxStatus.FAIL;
        return this;
    }

    public Outbox incrementCnt() {
        this.cnt = this.cnt + 1;
        return this;
    }
}
