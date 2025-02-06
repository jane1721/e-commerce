package com.jane.ecommerce.interfaces.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopItemResponse implements Serializable {

    Long id;
    String name;
    int soldCount;
}
