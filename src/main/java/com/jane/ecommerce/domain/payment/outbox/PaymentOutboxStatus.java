package com.jane.ecommerce.domain.payment.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentOutboxStatus {
    INIT,
    PUBLISHED,
    FAIL
}
