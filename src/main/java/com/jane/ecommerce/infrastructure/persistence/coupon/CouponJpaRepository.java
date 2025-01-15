package com.jane.ecommerce.infrastructure.persistence.coupon;

import com.jane.ecommerce.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
