package com.jane.ecommerce.domain.user;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.interfaces.dto.user.BalanceResponse;
import com.jane.ecommerce.interfaces.dto.user.ChargeRequest;
import com.jane.ecommerce.interfaces.dto.user.ChargeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    // 잔액 충전
    @Transactional
    public ChargeResponse chargeBalance(ChargeRequest request) {

        // 유저 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(request.getUserId()) }));

        // 도메인 엔티티 메서드 호출
        user.chargeBalance(request.getAmount());

        userRepository.save(user);

        return new ChargeResponse(user.getBalance());
    }

    // 잔액 조회
    public BalanceResponse getBalance(String userId) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ userId }));

        return new BalanceResponse(user.getId(), user.getBalance());
    }

    // 유저 조회
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(userId) })); // 유저가 없을 경우 예외 처리
    }

    // 잔액 차감
    @Transactional
    public void deductUserBalance(Long userId, BigDecimal amount) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(userId) }));

        // 도메인 엔티티 메서드 호출
        user.deductBalance(amount);

        userRepository.save(user);
    }

    // 유저 존재 여부 확인
    public boolean isUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
