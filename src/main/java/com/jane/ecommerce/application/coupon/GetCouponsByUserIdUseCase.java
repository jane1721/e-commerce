package com.jane.ecommerce.application.coupon;

import com.jane.ecommerce.domain.coupon.Coupon;
import com.jane.ecommerce.domain.coupon.CouponService;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.interfaces.dto.coupon.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GetCouponsByUserIdUseCase {

    private final CouponService couponService;

    @Transactional(readOnly = true)
    public List<CouponResponse> execute(String userId) {
        // 보유 쿠폰 조회
        List<UserCoupon> userCoupons = couponService.getAvailableCoupons(Long.valueOf(userId));

        // UserCoupon -> CouponResponse 로 변환
        List<CouponResponse> couponResponses = new ArrayList<>();

        for (UserCoupon userCoupon : userCoupons) {
            Coupon coupon = userCoupon.getCoupon();  // UserCoupon 에서 Coupon 추출
            CouponResponse couponResponse = new CouponResponse(
                    coupon.getCode(),            // 쿠폰 코드
                    coupon.getDiscountPercent(), // 할인율
                    coupon.getExpiryDate(),      // 만료일
                    userCoupon.getIsUsed()       // 사용 여부
            );
            couponResponses.add(couponResponse);  // CouponResponse 를 리스트에 추가
        }

        return couponResponses;  // 변환된 결과 리턴
    }
}
