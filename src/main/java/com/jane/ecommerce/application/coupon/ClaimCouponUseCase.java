package com.jane.ecommerce.application.coupon;

import com.jane.ecommerce.domain.coupon.CouponService;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.interfaces.dto.coupon.ClaimRequest;
import com.jane.ecommerce.interfaces.dto.coupon.ClaimResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClaimCouponUseCase {

    private final CouponService couponService;

    @Transactional
    public ClaimResponse execute(ClaimRequest claimRequest) {

        // 쿠폰 발급
        UserCoupon userCoupon = couponService.claimCoupon(claimRequest.getUserId(), claimRequest.getCouponId());

        // 발급된 쿠폰 정보를 응답 형식으로 변환
        return new ClaimResponse(userCoupon.getCoupon().getCode());
    }
}
