package com.jane.ecommerce.interfaces.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentRequest {
    Long userId;
    Long orderId;
    String method;
}
