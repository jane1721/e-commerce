package com.jane.ecommerce.application.payment;

import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.domain.item.Item;
import com.jane.ecommerce.domain.item.ItemService;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderItem;
import com.jane.ecommerce.domain.order.OrderService;
import com.jane.ecommerce.domain.payment.Payment;
import com.jane.ecommerce.domain.payment.PaymentService;
import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.user.UserService;
import com.jane.ecommerce.interfaces.dto.payment.PaymentCreateResponse;
import com.jane.ecommerce.interfaces.dto.payment.PaymentRequest;
import com.jane.ecommerce.interfaces.dto.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PaymentUseCase {

    private final PaymentService paymentService;
    private final UserService userService; // 사용자 잔액 관련 서비스
    private final OrderService orderService; // 주문 관련 서비스
    private final ItemService itemService; // 상품 재고 관련 서비스
    private final ExternalPlatformService externalPlatformService; // 외부 데이터 전송 서비스

    public PaymentCreateResponse processPayment(PaymentRequest paymentRequest) {

        // 사용자 잔액 조회
        User user = userService.getUserById(Long.parseLong(paymentRequest.getUserId()));

        // 주문 및 금액 확인
        Order order = orderService.getOrderById(Long.parseLong(paymentRequest.getOrderId()));
        Long finalAmount = order.getFinalAmount();

        try {
            // 잔액 차감
            userService.deductUserBalance(user.getId(), finalAmount);

        } catch (BaseCustomException e) {

            // 주문에 포함된 모든 상품의 재고 복구
            for (OrderItem orderItem : order.getOrderItems()) {
                Item item = orderItem.getItem();
                item.restoreStock(orderItem.getQuantity());  // 재고 복구
            }

            // 주문 상태 취소 변경감
            orderService.updateOrderStatus(order.getId(), "CANCELLED");

            return new PaymentCreateResponse(null, "CANCELLED", LocalDateTime.now());
        }

        // 결제 생성
        Payment payment = paymentService.createPayment(Long.parseLong(paymentRequest.getOrderId()), paymentRequest.getMethod());

        // 주문 상태 완료 변경
        orderService.updateOrderStatus(order.getId(), "COMPLETED");

        // 주문 정보 외부 데이터 플랫폼 전송
        externalPlatformService.sendOrderData(order);

        return new PaymentCreateResponse(payment.getId(), payment.getStatus(), payment.getUpdatedAt());
    }

    public PaymentResponse getPaymentStatus(String paymentId) {
        // 결제 상태 조회
        Payment payment = paymentService.getPaymentStatus(Long.valueOf(paymentId));

        return new PaymentResponse(
                payment.getId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getUpdatedAt()
        );
    }
}
