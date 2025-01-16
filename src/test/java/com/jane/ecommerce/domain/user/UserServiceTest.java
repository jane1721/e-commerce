package com.jane.ecommerce.domain.user;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.interfaces.dto.user.BalanceResponse;
import com.jane.ecommerce.interfaces.dto.user.ChargeRequest;
import com.jane.ecommerce.interfaces.dto.user.ChargeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock // 어노테이션 기반 Mock 객체 생성
    private UserRepository userRepository;

    @InjectMocks // 어노테이션 기반 의존성 주입
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        // 기본 유저 생성
        user = new User();
        user.setId(1L);
        user.setUsername("jane");
        user.setPassword("password");
        user.setBalance(BigDecimal.valueOf(1000L)); // 초기 잔액
    }

    // 잔액 충전 성공
    @Test
    void testChargeBalance_Success() {
        // given
        BigDecimal chargeAmount = BigDecimal.valueOf(500L);
        ChargeRequest request = new ChargeRequest(user.getId(), chargeAmount);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        ChargeResponse response = userService.chargeBalance(request);

        // test
        assertEquals(0, response.getCurrentBalance().compareTo(BigDecimal.valueOf(1500L))); // 충전 후 잔액 확인
        verify(userRepository).save(user); // 저장 메서드 호출 확인
    }

    // 음수 잔액 충전 요청 실패
    @Test
    void testChargeBalance_InvalidAmount() {
        // given
        BigDecimal invalidAmount = BigDecimal.valueOf(-500L);
        ChargeRequest request = new ChargeRequest(user.getId(), invalidAmount);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.chargeBalance(request);
        });

        assertEquals(ErrorCode.INVALID_PARAMETER, exception.getErrorCode());
        verify(userRepository, never()).save(user); // 저장 메서드가 호출되지 않았음을 확인
    }

    // 존재하지 않는 유저 잔액 충전 실패
    @Test
    void testChargeBalance_UserNotFound() {
        // given
        BigDecimal chargeAmount = BigDecimal.valueOf(500L);
        ChargeRequest request = new ChargeRequest(999999L, chargeAmount); // 존재하지 않는 사용자 ID

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.chargeBalance(request);
        });

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class)); // 저장 메서드가 호출되지 않았음을 확인
    }

    // 잔액 조회 성공
    @Test
    void testGetBalance_Success() {
        // given
        String userId = "1"; // 유저 ID
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        BalanceResponse response = userService.getBalance(userId);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(0, response.getBalance().compareTo(BigDecimal.valueOf(1000L))); // 잔액 확인
    }

    // 존재하지 않는 유저 잔액 조회 실패
    @Test
    void testGetBalance_UserNotFound() {
        // given
        String invalidUserId = "999999"; // 존재하지 않는 유저 ID
        when(userRepository.findById(999999L)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getBalance(invalidUserId);
        });

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    // 유저 조회 성공
    @Test
    public void testGetUserById_Success() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUserById(userId);

        // then
        assertNotNull(result);  // 유저가 반환되어야 함
        verify(userRepository, times(1)).findById(userId); // findById 메서드가 한번 호출되었는지 확인
    }

    // 존재하지 않는 유저 조회 실패
    @Test
    public void testGetUserById_NotFound() {
        // given
        long userId = 999999L;

        // 유저가 존재하지 않는 경우 (Optional.empty() 반환)
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when, then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode()); // 예외 코드 확인
    }

    // 잔액 차감 성공
    @Test
    void testDeductUserBalance_Success() {
        // given
        BigDecimal deductAmount = BigDecimal.valueOf(500L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        userService.deductUserBalance(user.getId(), deductAmount);

        // then
        assertEquals(deductAmount, user.getBalance()); // 차감 후 잔액 확인
        verify(userRepository).save(user); // 저장 메서드 호출 확인
    }

    // 잔액 부족으로 차감 실패
    @Test
    void testDeductUserBalance_InsufficientBalance() {
        // given
        BigDecimal deductAmount = BigDecimal.valueOf(1500L); // 잔액보다 많은 금액 차감 요청
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.deductUserBalance(user.getId(), deductAmount);
        });

        assertEquals(ErrorCode.INSUFFICIENT_BALANCE, exception.getErrorCode()); // 예외 코드 확인
        assertEquals(BigDecimal.valueOf(1000L), user.getBalance()); // 잔액이 변경되지 않았음을 확인
        verify(userRepository, never()).save(user); // 저장 메서드가 호출되지 않았음을 확인
    }

    // 존재하지 않는 유저로 인해 차감 실패
    @Test
    void testDeductUserBalance_UserNotFound() {
        // given
        BigDecimal deductAmount = BigDecimal.valueOf(500L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.deductUserBalance(999999L, deductAmount); // 존재하지 않는 유저 ID
        });

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode()); // 예외 코드 확인
        verify(userRepository, never()).save(any(User.class)); // 저장 메서드가 호출되지 않았음을 확인
    }
}
