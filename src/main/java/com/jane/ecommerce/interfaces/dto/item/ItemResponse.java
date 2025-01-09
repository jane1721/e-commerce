package com.jane.ecommerce.interfaces.dto.item;

import com.jane.ecommerce.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemResponse {

    String itemId;
    String name;
    Long price;
    int stock;

    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId().toString(), item.getName(), item.getPrice(), item.getStock());
    }
}
