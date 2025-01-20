package com.jane.ecommerce.interfaces.dto.order;

import com.jane.ecommerce.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateResponse {

    String id;
    OrderStatus status;
    LocalDateTime updatedAt;
}
