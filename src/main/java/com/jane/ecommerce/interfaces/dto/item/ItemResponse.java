package com.jane.ecommerce.interfaces.dto.item;

import com.jane.ecommerce.domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ItemResponse implements Serializable {

    Long id;
    String name;
    BigDecimal price;
    int stock;

    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getPrice(), item.getStock());
    }
}
