package com.jane.ecommerce.domain.coupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findById(Long id);
    Coupon save(Coupon coupon);
    Optional<Coupon> findByIdWithPessimisticLock(Long id);
}
