package com.jane.ecommerce.domain.outbox;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboxStatus {
    INIT,
    PUBLISHED,
    FAIL;
}
