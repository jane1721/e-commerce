package com.jane.ecommerce.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
    List<UserCoupon> findAvailableCouponsByUserId(Long userId);  // 사용자 ID로 발급된 쿠폰 조회
    UserCoupon save(UserCoupon userCoupon);
    Optional<UserCoupon> findById(long userCouponId);
    List<UserCoupon> findAll();
}
