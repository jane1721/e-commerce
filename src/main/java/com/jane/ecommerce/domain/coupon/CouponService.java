package com.jane.ecommerce.domain.coupon;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    // 쿠폰 발급
    @Transactional
    public UserCoupon claimCoupon(Long userId, Long couponId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ String.valueOf(userId) }));

        // 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ String.valueOf(couponId) }));

        // 쿠폰 발급
        coupon.claim();

        couponRepository.save(coupon);

        // 사용자에게 쿠폰 발급 기록 저장
        UserCoupon userCoupon = UserCoupon.create(user, coupon, false);

        return userCouponRepository.save(userCoupon);
    }

    // 보유 쿠폰 조회 (만료된 쿠폰은 제외)
    public List<UserCoupon> getAvailableCoupons(Long userId) {

        return userCouponRepository.findAvailableCouponsByUserId(userId);
    }

    // 유저 쿠폰 단 건 조회
    public UserCoupon getUserCouponById(long userCouponId) {
        return userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ String.valueOf(userCouponId) })); // 유저가 없을 경우 예외 처리
    }
}
