package com.jane.ecommerce.application.coupon;

import com.jane.ecommerce.domain.coupon.CouponService;
import com.jane.ecommerce.interfaces.dto.coupon.ClaimRequest;
import com.jane.ecommerce.interfaces.dto.coupon.ClaimResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AddCouponRequestToQueueUseCase {

    private final CouponService couponService;

    public ClaimResponse execute(ClaimRequest claimRequest) {

        // 쿠폰 발급 요청을 대기열에 추가
        couponService.addCouponRequestToQueue(claimRequest.getUserId(), claimRequest.getCouponId());

        return new ClaimResponse(claimRequest.getCouponId().toString());
    }
}
