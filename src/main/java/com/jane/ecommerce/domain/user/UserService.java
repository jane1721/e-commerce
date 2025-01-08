package com.jane.ecommerce.domain.user;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.interfaces.dto.user.BalanceResponse;
import com.jane.ecommerce.interfaces.dto.user.ChargeRequest;
import com.jane.ecommerce.interfaces.dto.user.ChargeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 잔액 충전
    public ChargeResponse chargeBalance(ChargeRequest request) {

        // 유저 조회
        User user = userRepository.findById(Long.parseLong(request.getUserId()))
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ request.getUserId() }));

        // 도메인 엔티티 메서드 호출
        user.chargeBalance(request.getAmount());

        userRepository.save(user);

        return new ChargeResponse(user.getBalance());
    }

    // 잔액 조회
    public BalanceResponse getBalance(String userId) {
        User user = userRepository.findById(Long.parseLong(userId))
            .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ userId }));

        return new BalanceResponse(user.getId(), user.getBalance());
    }
}
