package com.jane.ecommerce.domain.coupon;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.error.CustomException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CouponTest {

    @Test
    void testClaimCoupon_Success() {
        // Given
        Coupon coupon = Coupon.create(null, null, null, 5, null); // 쿠폰 수량 5로 설정

        // When
        coupon.claim(); // claim 메서드 호출

        // Then
        assertEquals(4, coupon.getQuantity(), "쿠폰 수량이 1 감소해야 합니다.");
    }

    @Test
    void testClaimCoupon_QuantityZero() {
        // Given
        Coupon coupon = Coupon.create(null, null, null, 0, null); // 쿠폰 수량 0으로 설정

        // When & Then
        CustomException exception = assertThrows(CustomException.class, coupon::claim);

        // 예외 코드 확인
        assertEquals(ErrorCode.CONFLICT, exception.getErrorCode(), "예외 코드가 일치해야 합니다.");
    }
}
