package com.jane.ecommerce.interfaces.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemDTO {

    Long itemId;
    int quantity;
}
