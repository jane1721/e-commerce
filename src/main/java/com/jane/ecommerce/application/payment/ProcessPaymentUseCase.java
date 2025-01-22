package com.jane.ecommerce.application.payment;

import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderClient;
import com.jane.ecommerce.domain.order.OrderService;
import com.jane.ecommerce.domain.order.OrderStatus;
import com.jane.ecommerce.domain.payment.Payment;
import com.jane.ecommerce.domain.payment.PaymentService;
import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.user.UserService;
import com.jane.ecommerce.interfaces.dto.payment.PaymentCreateResponse;
import com.jane.ecommerce.interfaces.dto.payment.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProcessPaymentUseCase {

    private final PaymentService paymentService;
    private final UserService userService; // 사용자 잔액 관련 서비스
    private final OrderService orderService; // 주문 관련 서비스
    private final OrderClient orderClient; // 외부 데이터 전송 서비스

    @Transactional
    public PaymentCreateResponse execute(PaymentRequest paymentRequest) {

        // 사용자 잔액 조회
        User user = userService.getUserByIdWithLock(Long.parseLong(paymentRequest.getUserId()));

        // 주문 최종 금액 확인
        Order order = orderService.getOrderById(Long.parseLong(paymentRequest.getOrderId()));

        // 잔액 차감
        userService.deductUserBalance(user.getId(), order.getFinalAmount());

        // 결제 생성
        Payment payment = paymentService.createPayment(Long.parseLong(paymentRequest.getOrderId()), paymentRequest.getMethod());

        // 주문 상태 완료 변경
        orderService.updateOrderStatus(order.getId(), OrderStatus.COMPLETED);

        // 주문 정보 외부 데이터 플랫폼 전송
        boolean isSend = orderClient.send(order);

        if (!isSend) {
            throw new CustomException(ErrorCode.CONFLICT);
        }

        return new PaymentCreateResponse(payment.getId(), payment.getUpdatedAt());
    }
}
