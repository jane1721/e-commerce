package com.jane.ecommerce.interfaces.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItemResponse {

    String itemId;
    String name;
    int quantity;
    int price;
}
