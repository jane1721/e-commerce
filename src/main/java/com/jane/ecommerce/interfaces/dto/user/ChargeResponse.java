package com.jane.ecommerce.interfaces.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ChargeResponse {
    BigDecimal currentBalance;
}
