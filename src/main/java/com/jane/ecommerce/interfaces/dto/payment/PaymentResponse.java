package com.jane.ecommerce.interfaces.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PaymentResponse {
    Long id;
    String status;
    Long amount;
    String method;
    LocalDateTime updatedAt;
}
