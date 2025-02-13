package com.jane.ecommerce.interfaces.listener;

import com.jane.ecommerce.application.payment.PaymentProcessedEvent;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class PaymentProcessedListener {

    private final OrderClient orderClient;

    @Async // 비동기 처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 트랜잭션 커밋 후에 실행
    public void handlePaymentProcessedEvent(PaymentProcessedEvent event) {
        Order order = event.getOrder();

        // 주문 정보를 외부 데이터 플랫폼으로 전송
        boolean isSent = orderClient.send(order);

        if (!isSent) {
            throw new CustomException(ErrorCode.CONFLICT);
        }
    }
}
