package com.jane.ecommerce.interfaces.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BalanceResponse {

    Long userId;
    Long balance;
}
