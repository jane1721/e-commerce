package com.jane.ecommerce.infrastructure.persistence.coupon;

import com.jane.ecommerce.domain.coupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    // 만료일이 지나지 않았고, 사용되지 않은 쿠폰만 조회
    @Query("SELECT uc FROM UserCoupon uc " +
            " JOIN uc.coupon c " +
            "WHERE uc.user.id = :userId " +
            " AND c.expiryDate > CURRENT_TIMESTAMP " +
            " AND uc.isUsed = false")
    List<UserCoupon> findAvailableCouponsByUserId(Long userId); // 사용자 ID로 발급된 쿠폰 조회
}
