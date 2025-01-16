package com.jane.ecommerce.domain.coupon;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void testClaimCoupon_Success() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        User user = new User();
        user.setId(userId);

        Coupon coupon = Coupon.of(couponId, null, null, null, 10, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(userCouponRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UserCoupon result = couponService.claimCoupon(userId, couponId);

        // then
        assertNotNull(result);
        assertEquals(coupon, result.getCoupon());
        assertEquals(user, result.getUser());
        assertFalse(result.getIsUsed());
        verify(couponRepository).save(coupon);
    }

    @Test
    void testClaimCoupon_UserNotFound() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> couponService.claimCoupon(userId, couponId));

        // then
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        verify(userRepository).findById(userId);
        verifyNoInteractions(couponRepository, userCouponRepository);
    }

    @Test
    void testClaimCoupon_CouponNotFound() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> couponService.claimCoupon(userId, couponId));

        // then
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        verify(userRepository).findById(userId);
        verify(couponRepository).findById(couponId);
        verifyNoInteractions(userCouponRepository);
    }

    @Test
    void testClaimCoupon_QuantityZero() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        User user = new User();
        user.setId(userId);

        Coupon coupon = Coupon.of(couponId, null, null, null, 0, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> couponService.claimCoupon(userId, couponId));

        // then
        assertEquals(ErrorCode.CONFLICT, exception.getErrorCode());
        verify(userRepository).findById(userId);
        verify(couponRepository).findById(couponId);
        verifyNoInteractions(userCouponRepository);
    }

    @Test
    void testGetAvailableCoupons_Success() {
        // given
        Long userId = 1L;
        List<UserCoupon> mockCoupons = List.of(new UserCoupon(), new UserCoupon());

        when(userCouponRepository.findAvailableCouponsByUserId(userId)).thenReturn(mockCoupons);

        // when
        List<UserCoupon> result = couponService.getAvailableCoupons(userId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userCouponRepository).findAvailableCouponsByUserId(userId);
    }

    // 유저 조회 성공
    @Test
    public void testGetUserCouponById_Success() {
        // given
        long userCouponId = 1L;
        UserCoupon userCoupon = new UserCoupon();
        when(userCouponRepository.findById(userCouponId)).thenReturn(Optional.of(userCoupon));

        // when
        UserCoupon result = couponService.getUserCouponById(userCouponId);

        // then
        assertNotNull(result);  // 유저가 반환되어야 함
        verify(userCouponRepository, times(1)).findById(userCouponId); // findById 메서드가 한번 호출되었는지 확인
    }

    // 존재하지 않는 유저 조회 실패
    @Test
    public void testGetUserById_NotFound() {
        // given
        long userCouponId = 999999L;

        // 유저가 존재하지 않는 경우 (Optional.empty() 반환)
        when(userCouponRepository.findById(userCouponId)).thenReturn(Optional.empty());

        // when, then
        CustomException exception = assertThrows(CustomException.class, () -> {
            couponService.getUserCouponById(userCouponId);
        });

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode()); // 예외 코드 확인
    }
}
