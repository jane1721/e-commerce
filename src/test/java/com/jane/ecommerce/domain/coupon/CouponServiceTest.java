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

    @Mock
    private CouponQueueRepository couponQueueRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void testAddCouponRequestToQueue_Success() {
        // given
        Long userId = 1L;
        Long couponId = 100L;

        Coupon coupon = Coupon.of(couponId, null, null, null, 50);

        // mocking
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponQueueRepository.isCouponAvailable(couponId, coupon.getQuantity())).thenReturn(true);
        when(couponQueueRepository.isDuplicateRequest(userId, couponId)).thenReturn(false);
        doNothing().when(couponQueueRepository).addToQueue(userId, couponId);

        // when
        assertDoesNotThrow(() -> couponService.addCouponRequestToQueue(userId, couponId));

        // then
        verify(couponRepository, times(1)).findById(couponId);
        verify(couponQueueRepository, times(1)).isCouponAvailable(couponId, coupon.getQuantity());
        verify(couponQueueRepository, times(1)).isDuplicateRequest(userId, couponId);
        verify(couponQueueRepository, times(1)).addToQueue(userId, couponId);
    }

    @Test
    void testAddCouponRequestToQueue_InsufficientCouponStock() {
        // given
        Long userId = 1L;
        Long couponId = 100L;

        Coupon coupon = Coupon.of(couponId, null, null, null, 50);

        // mocking
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponQueueRepository.isCouponAvailable(couponId, coupon.getQuantity())).thenReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> couponService.addCouponRequestToQueue(userId, couponId));

        // then
        assertEquals(ErrorCode.INSUFFICIENT_COUPON_STOCK, exception.getErrorCode());
        verify(couponRepository, times(1)).findById(couponId);
        verify(couponQueueRepository, times(1)).isCouponAvailable(couponId, coupon.getQuantity());
        verify(couponQueueRepository, never()).isDuplicateRequest(userId, couponId); // 재고 부족 시 이후 로직 타지 않음
        verify(couponQueueRepository, never()).addToQueue(userId, couponId);
    }

    @Test
    void testAddCouponRequestToQueue_DuplicateCouponClaim() {
        // given
        Long userId = 1L;
        Long couponId = 100L;

        Coupon coupon = Coupon.of(couponId, null, null, null, 50);

        // mocking
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponQueueRepository.isCouponAvailable(couponId, coupon.getQuantity())).thenReturn(true);
        when(couponQueueRepository.isDuplicateRequest(userId, couponId)).thenReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> couponService.addCouponRequestToQueue(userId, couponId));

        // then
        assertEquals(ErrorCode.DUPLICATE_COUPON_CLAIM, exception.getErrorCode());
        verify(couponRepository, times(1)).findById(couponId);
        verify(couponQueueRepository, times(1)).isCouponAvailable(couponId, coupon.getQuantity());
        verify(couponQueueRepository, times(1)).isDuplicateRequest(userId, couponId);
        verify(couponQueueRepository, never()).addToQueue(userId, couponId); // 중복 요청 시 대기열에 추가되지 않음
    }

    @Test
    void testClaimCoupon_Success() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        User user = User.of(userId, null, null, null);

        Coupon coupon = Coupon.of(couponId, null, null, null, 10);

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

        User user = User.of(userId, null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> couponService.claimCoupon(userId, couponId));

        // then
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        verify(userRepository).findById(userId);
        verify(couponRepository).findByIdWithPessimisticLock(couponId);
        verifyNoInteractions(userCouponRepository);
    }

    @Test
    void testClaimCoupon_QuantityZero() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        User user = User.of(userId, null, null, null);

        Coupon coupon = Coupon.of(couponId, null, null, null, 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> couponService.claimCoupon(userId, couponId));

        // then
        assertEquals(ErrorCode.CONFLICT, exception.getErrorCode());
        verify(userRepository).findById(userId);
        verify(couponRepository).findByIdWithPessimisticLock(couponId);
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
        CustomException exception = assertThrows(CustomException.class, () -> couponService.getUserCouponById(userCouponId));

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode()); // 예외 코드 확인
    }
}
