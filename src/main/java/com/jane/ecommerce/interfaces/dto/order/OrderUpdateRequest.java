package com.jane.ecommerce.interfaces.dto.order;

import com.jane.ecommerce.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderUpdateRequest {

    OrderStatus status;
}
