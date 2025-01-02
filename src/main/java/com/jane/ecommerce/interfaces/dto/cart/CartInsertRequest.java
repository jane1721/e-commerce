package com.jane.ecommerce.interfaces.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartInsertRequest {

    String userId;
    String itemId;
    int quantity;
}
