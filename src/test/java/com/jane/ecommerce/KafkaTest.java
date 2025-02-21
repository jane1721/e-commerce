package com.jane.ecommerce;

import com.jane.ecommerce.domain.coupon.Coupon;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderClient;
import com.jane.ecommerce.domain.order.OrderStatus;
import com.jane.ecommerce.domain.outbox.OutboxService;
import com.jane.ecommerce.domain.outbox.OutboxStatus;
import com.jane.ecommerce.domain.user.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import com.jane.ecommerce.infrastructure.messaging.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class KafkaTest {
    private static final String TOPIC = "test";
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final AtomicReference<String> receivedMessage = new AtomicReference<>(); // 수신된 메시지 저장

    @Autowired
    private KafkaTemplate<String, Object> producer;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private OrderClient orderClient;

    @KafkaListener(topics = TOPIC)
    public void listen(String message) {
        counter.incrementAndGet(); // 리스너가 호출되면 1 증가
        receivedMessage.set(message); // 수신된 메시지 저장
    }

    @Test
    void kafkaTest() {
        String expectedMessage = "Hello, Jane!";
        producer.send(TOPIC, expectedMessage);

        await() // 검증될 때 까지 기다린다.
            .pollInterval(Duration.ofMillis(300)) // 300ms 마다 한 번씩
            .atMost(Duration.ofSeconds(2)) // 최대 2s 까지
            .untilAsserted(() -> { // 아래 모든 assertion 이 만족할 때 까지
                System.out.println("counter.get() = " + counter.get());
                System.out.println("receivedMessage.get() = " + receivedMessage.get());

                assertThat(counter.get()).isEqualTo(1L);
                assertThat(receivedMessage.get().replaceAll("^\"|\"$", "")).isEqualTo(expectedMessage);
            });
    }

    @Test
    void kafkaProducerServiceTest() {
        kafkaProducerService.sendMessage(TOPIC, "test-1", "Hello, Jane!");

        await() // 검증될 때 까지 기다린다.
            .pollInterval(Duration.ofMillis(300)) // 300ms 마다 한 번씩
            .atMost(Duration.ofSeconds(2)) // 최대 2s 까지
            .untilAsserted(() -> { // 아래 모든 assertion 이 만족할 때 까지
                System.out.println("counter.get() = " + counter.get());
                assertThat(counter.get()).isEqualTo(1L);
            });
    }

    @Test
    void kafkaPaymentProcessEventTest() {

        User user = User.create("jane", "password", BigDecimal.valueOf(1000L));
        Coupon coupon = Coupon.create("code-1", 10L, LocalDateTime.of(2022, 12, 31, 23, 59, 59), 1);
        UserCoupon userCoupon = UserCoupon.create(user, coupon, false);

        Order order = Order.create(
            user,
            userCoupon,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            OrderStatus.COMPLETED
        );

        kafkaProducerService.sendMessage("local.commerce.payment-process-event.v1", "test-2", order);

        await() // 검증될 때 까지 기다린다.
            .pollInterval(Duration.ofMillis(300)) // 300ms 마다 한 번씩
            .atMost(Duration.ofSeconds(2)) // 최대 2s 까지
            .untilAsserted(() -> { // 아래 모든 assertion 이 만족할 때 까지

                // 아웃박스 데이터 상태 변경 확인
                assertThat(outboxService.findById("test-2").getStatus()).isEqualTo(OutboxStatus.PUBLISHED);

                // 주문 정보 외부 데이터 플랫폼 전송 로직 호출 검증
                verify(orderClient).send(order);
            });
    }
}