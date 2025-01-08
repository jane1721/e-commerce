package com.jane.ecommerce.domain.user;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.interfaces.dto.user.BalanceResponse;
import com.jane.ecommerce.interfaces.dto.user.ChargeRequest;
import com.jane.ecommerce.interfaces.dto.user.ChargeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        // 기본 사용자 생성
        user = new User();
        user.setId(1L);
        user.setUsername("jane");
        user.setPassword("password");
        user.setBalance(1000L); // 초기 잔액
    }

    // 잔액 충전 성공
    @Test
    void testChargeBalance_Success() {
        // given
        long chargeAmount = 500L;
        ChargeRequest request = new ChargeRequest(String.valueOf(user.getId()), chargeAmount);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        ChargeResponse response = userService.chargeBalance(request);

        // test
        assertEquals(1500L, response.getCurrentBalance()); // 충전 후 잔액 확인
        verify(userRepository).save(user); // 저장 메서드 호출 확인
    }

    // 음수 잔액 충전 요청 실패
    @Test
    void testChargeBalance_InvalidAmount() {
        // given
        long invalidAmount = -500L;
        ChargeRequest request = new ChargeRequest(String.valueOf(user.getId()), invalidAmount);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when & then
        BaseCustomException exception = assertThrows(BaseCustomException.class, () -> {
            userService.chargeBalance(request);
        });

        assertEquals(BaseErrorCode.INVALID_PARAMETER, exception.getBaseErrorCode());
        verify(userRepository, never()).save(user); // 저장 메서드가 호출되지 않았음을 확인
    }

    // 존재하지 않는 사용자 잔액 충전 실패
    @Test
    void testChargeBalance_UserNotFound() {
        // given
        long chargeAmount = 500L;
        ChargeRequest request = new ChargeRequest("999999", chargeAmount); // 존재하지 않는 사용자 ID

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        BaseCustomException exception = assertThrows(BaseCustomException.class, () -> {
            userService.chargeBalance(request);
        });

        assertEquals(BaseErrorCode.NOT_FOUND, exception.getBaseErrorCode());
        verify(userRepository, never()).save(any(User.class)); // 저장 메서드가 호출되지 않았음을 확인
    }

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
        assertEquals(1000L, response.getBalance());
    }

    @Test
    void testGetBalance_UserNotFound() {
        // given
        String invalidUserId = "999999"; // 존재하지 않는 사용자 ID
        when(userRepository.findById(999999L)).thenReturn(java.util.Optional.empty());

        // when & then
        BaseCustomException exception = assertThrows(BaseCustomException.class, () -> {
            userService.getBalance(invalidUserId);
        });

        assertEquals(BaseErrorCode.NOT_FOUND, exception.getBaseErrorCode());
    }
}
