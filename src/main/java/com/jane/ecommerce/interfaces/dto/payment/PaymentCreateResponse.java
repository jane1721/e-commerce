package com.jane.ecommerce.interfaces.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PaymentCreateResponse {

    Long id;
    LocalDateTime createdAt;
}
