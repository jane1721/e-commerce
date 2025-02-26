package com.jane.ecommerce.application.payment;

import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderService;
import com.jane.ecommerce.domain.order.OrderStatus;
import com.jane.ecommerce.domain.payment.Payment;
import com.jane.ecommerce.domain.payment.PaymentService;
import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.user.UserService;
import com.jane.ecommerce.interfaces.dto.payment.PaymentCreateResponse;
import com.jane.ecommerce.interfaces.dto.payment.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@EnableAsync  // 비동기 처리를 위한 어노테이션
@RequiredArgsConstructor
@Service
public class ProcessPaymentUseCase {

    private final PaymentService paymentService;
    private final UserService userService; // 사용자 잔액 관련 서비스
    private final OrderService orderService; // 주문 관련 서비스
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행자

    @Transactional
    public PaymentCreateResponse execute(PaymentRequest paymentRequest) {

        // 사용자 잔액 조회
        User user = userService.getUserByIdWithLock(paymentRequest.getUserId());

        // 주문 최종 금액 확인
        Order order = orderService.getOrderById(paymentRequest.getOrderId());

        // 잔액 차감
        userService.deductUserBalance(user.getId(), order.getFinalAmount());

        // 결제 생성
        Payment payment = paymentService.createPayment(paymentRequest.getOrderId(), paymentRequest.getMethod());

        // 주문 상태 완료 변경
        orderService.updateOrderStatus(order.getId(), OrderStatus.COMPLETED);

        // 결제 완료 이벤트 발행
        eventPublisher.publishEvent(new PaymentProcessedEvent(this, order));

        return new PaymentCreateResponse(payment.getId(), payment.getUpdatedAt());
    }
}
