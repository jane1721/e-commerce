package com.jane.ecommerce.interfaces.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderCreateRequest {

    Long userId;
    List<OrderItemDTO> orderItems;
    Long userCouponId;
}
