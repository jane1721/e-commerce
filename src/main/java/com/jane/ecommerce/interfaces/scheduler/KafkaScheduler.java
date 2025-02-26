package com.jane.ecommerce.interfaces.scheduler;

import com.jane.ecommerce.application.payment.outbox.PaymentOutboxFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class KafkaScheduler {

    private final PaymentOutboxFacade paymentOutboxFacade;

    // 5분마다 실행
    @Scheduled(cron = "0 */5 * * * *")
    public void run() {
        // 스케줄러로 아웃박스 테이블에서 상태가 미전송인걸 확인해서 재발송 (=브로커에 메세지를 다시 produce) 한다.
        paymentOutboxFacade.retryOutboxMessage(LocalDateTime.now().minusMinutes(5));
    }
}
