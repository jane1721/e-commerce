package com.jane.ecommerce.interfaces.dto.order;

import com.jane.ecommerce.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    Long id;
    OrderStatus status;
    List<OrderItemDTO> orderItems;
    BigDecimal totalAmount;
    LocalDateTime createAt;
}
