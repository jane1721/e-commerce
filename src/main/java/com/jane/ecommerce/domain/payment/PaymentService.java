package com.jane.ecommerce.domain.payment;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // 결제 데이터 생성
    public Payment createPayment(Long orderId, String method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ String.valueOf(orderId) }));

        Long amount = order.getTotalAmount();

        Payment payment = new Payment(null, order, amount, method, "INITIATED");

        paymentRepository.save(payment);

        return payment;
    }

    // 결제 상태 조회
    public Payment getPaymentStatus(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ String.valueOf(paymentId) }));
    }

}
