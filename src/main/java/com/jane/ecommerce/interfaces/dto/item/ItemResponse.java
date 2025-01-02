package com.jane.ecommerce.interfaces.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemResponse {

    String itemId;
    String name;
    int price;
    int stock;
}
