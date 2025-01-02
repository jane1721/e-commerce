package com.jane.ecommerce.interfaces.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrderStatusDTO {

    String id;
    String status;
    LocalDateTime updatedAt;
}