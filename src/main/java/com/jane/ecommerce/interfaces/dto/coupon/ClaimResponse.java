package com.jane.ecommerce.interfaces.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClaimResponse {

    String status;
    String message;
    String code;
}
