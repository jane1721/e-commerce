package com.jane.ecommerce.interfaces.scheduler;

import com.jane.ecommerce.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CouponScheduler {

    private final CouponService couponService;

    // 발급할 쿠폰 ID 목록을 순차적으로 처리
    @Scheduled(cron = "*/1 * * * * *") // 1초마다 실행
    public void processCouponQueue() {
        Long couponId = 3L;  // 쿠폰 ID
        int claimCount = 100;  // 한 번에 발급할 수량

        couponService.processCouponQueue(couponId, claimCount);
    }
}
