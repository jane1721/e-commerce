package com.jane.ecommerce.interfaces.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopItemResponse {

    String id;
    String name;
    int soldCount;
}
