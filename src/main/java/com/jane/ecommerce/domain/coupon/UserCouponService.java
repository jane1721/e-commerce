package com.jane.ecommerce.domain.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    @Transactional
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponRepository.save(userCoupon);
    }
}
