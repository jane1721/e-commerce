package com.jane.ecommerce.interfaces.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponse {

    String orderId;
    String status;
    List<OrderItemDTO> orderItems;
    int totalAmount;
    LocalDateTime createAt;
}
