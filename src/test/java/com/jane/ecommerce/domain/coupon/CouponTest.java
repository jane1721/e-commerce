package com.jane.ecommerce.domain.coupon;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CouponTest {

    @Test
    void testClaimCoupon_Success() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setQuantity(5);  // 쿠폰 수량 5로 설정

        // When
        coupon.claim();  // claim 메서드 호출

        // Then
        assertEquals(4, coupon.getQuantity(), "쿠폰 수량이 1 감소해야 합니다.");
    }

    @Test
    void testClaimCoupon_QuantityZero() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setQuantity(0);  // 쿠폰 수량 0으로 설정

        // When & Then
        BaseCustomException exception = assertThrows(BaseCustomException.class, coupon::claim);

        // 예외 코드 확인
        assertEquals(BaseErrorCode.CONFLICT, exception.getBaseErrorCode(), "예외 코드가 일치해야 합니다.");
    }
}
