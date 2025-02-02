package com.jane.ecommerce.interfaces.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopItemResponse {

    Long id;
    String name;
    int soldCount;
}
