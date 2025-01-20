package com.jane.ecommerce.domain.payment;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // 결제 데이터 생성
    @Transactional
    public Payment createPayment(Long orderId, String method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(orderId) }));

        BigDecimal amount = order.getTotalAmount();

        Payment payment = Payment.of(null, order, amount, method);

        return paymentRepository.save(payment);
    }

    // 결제 상태 조회
    public Payment getPaymentStatus(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(paymentId) }));
    }

}
