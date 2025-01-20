package com.jane.ecommerce.interfaces.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponse {
    Long id;
    BigDecimal amount;
    String method;
    LocalDateTime updatedAt;
}
