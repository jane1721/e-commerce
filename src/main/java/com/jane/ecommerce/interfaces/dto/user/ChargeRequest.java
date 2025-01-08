package com.jane.ecommerce.interfaces.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChargeRequest {

    String userId;
    Long amount;
}
