package com.jane.ecommerce.interfaces.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrderCreateResponse {

    String id;
    String status;
    int totalAmount;
    LocalDateTime createdAt;
}
