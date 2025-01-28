package com.jane.ecommerce.interfaces.dto.order;

import com.jane.ecommerce.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrderCreateResponse {

    Long id;
    OrderStatus status;
    int totalAmount;
    LocalDateTime createdAt;
}
