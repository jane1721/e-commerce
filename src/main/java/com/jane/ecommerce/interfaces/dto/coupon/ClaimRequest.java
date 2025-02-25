package com.jane.ecommerce.interfaces.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClaimRequest {

    Long userId;
    Long couponId;
}
