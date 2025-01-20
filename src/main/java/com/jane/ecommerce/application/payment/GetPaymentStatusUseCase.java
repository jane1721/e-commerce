package com.jane.ecommerce.application.payment;

import com.jane.ecommerce.domain.payment.Payment;
import com.jane.ecommerce.domain.payment.PaymentService;
import com.jane.ecommerce.interfaces.dto.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GetPaymentStatusUseCase {
    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public PaymentResponse execute(String paymentId) {
        // 결제 상태 조회
        Payment payment = paymentService.getPaymentStatus(Long.valueOf(paymentId));

        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getUpdatedAt()
        );
    }
}
