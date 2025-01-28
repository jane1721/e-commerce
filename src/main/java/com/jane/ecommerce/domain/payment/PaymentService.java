package com.jane.ecommerce.domain.payment;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // 결제 데이터 생성
    @Transactional
    public Payment createPayment(Long orderId, String method) {

        // 결제한 주문이 이미 처리되었는지 확인
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
        if (existingPayment.isPresent()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(orderId) }));

        BigDecimal amount = order.getTotalAmount();

        Payment payment = Payment.of(null, order, amount, method);

        return paymentRepository.save(payment);
    }

    // 결제 상태 조회
    public Payment getPaymentStatus(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(id) }));
    }

}
