package com.jane.ecommerce.domain.payment.outbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PaymentOutbox {
    private final String id;
    private final String message;
    private PaymentOutboxStatus status;
    private int count;

    public static PaymentOutbox init(String message) {
        return PaymentOutbox.builder()
                .id(UUID.randomUUID().toString())
                .message(message)
                .status(PaymentOutboxStatus.INIT)
                .count(0)
                .build();
    }

    public PaymentOutbox published() {
        this.status = PaymentOutboxStatus.PUBLISHED;
        return this;
    }

    public PaymentOutbox failed() {
        this.status = PaymentOutboxStatus.FAIL;
        return this;
    }

    public PaymentOutbox incrementCount() {
        this.count = this.count + 1;
        return this;
    }
}
