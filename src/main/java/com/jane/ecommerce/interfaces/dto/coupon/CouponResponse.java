package com.jane.ecommerce.interfaces.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CouponResponse {

    String code;
    int discountPercent;
    LocalDateTime expiryDate;
    boolean isUsed;
}
