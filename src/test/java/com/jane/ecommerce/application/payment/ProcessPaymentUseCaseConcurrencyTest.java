package com.jane.ecommerce.application.payment;

import com.jane.ecommerce.application.IntegrationTest;
import com.jane.ecommerce.domain.item.Item;
import com.jane.ecommerce.domain.order.OrderItem;
import com.jane.ecommerce.domain.order.OrderStatus;
import com.jane.ecommerce.domain.payment.PaymentRepository;
import com.jane.ecommerce.domain.user.UserRepository;
import com.jane.ecommerce.domain.order.OrderRepository;
import com.jane.ecommerce.domain.item.ItemRepository;
import com.jane.ecommerce.domain.payment.Payment;
import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.interfaces.dto.payment.PaymentRequest;
import com.jane.ecommerce.interfaces.dto.payment.PaymentCreateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessPaymentUseCaseConcurrencyTest extends IntegrationTest {

    @Autowired
    private ProcessPaymentUseCase processPaymentUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("10명이 동시에 결제하더라도, 주문은 하나만 처리되어야 한다.")
    void testConcurrentProcessPayment() {
        // given: 테스트용 유저, 주문, 결제 요청 생성
        User user = userRepository.save(User.create("testUser@example.com", "password", BigDecimal.valueOf(1000))); // 충분한 잔액
        Item item = itemRepository.save(Item.create("Test Item", BigDecimal.valueOf(100), 50)); // 이름, 가격, 재고
        Order order = orderRepository.save(Order.create(user, null, BigDecimal.valueOf(200), BigDecimal.valueOf(200), OrderStatus.PENDING));
        OrderItem orderItem = OrderItem.create(item, 2);
        order.addOrderItem(orderItem);

        order = orderRepository.save(order);

        PaymentRequest paymentRequest = new PaymentRequest(order.getId().toString(), user.getId().toString(), "CREDIT_CARD");

        // 동시성 테스트 준비
        int concurrentRequests = 10;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        CompletableFuture<?>[] futures = new CompletableFuture[concurrentRequests];

        // when: 동시 요청 실행
        for (int i = 0; i < concurrentRequests; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    PaymentCreateResponse response = processPaymentUseCase.execute(paymentRequest);
                    assertThat(response).isNotNull();
                } catch (Exception e) {
                    // 예외 처리: 중복 결제는 예외로 처리하지 않고 무시 (동시성 제어)
                }
            }, executor);
        }

        CompletableFuture.allOf(futures).join();
        executor.shutdown();

        // then: 동시성 테스트 후 결제 확인
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1); // 결제는 하나만 처리되어야 함 (중복 결제 방지)

        // 주문 상태 확인
        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

        // 사용자의 잔액 확인
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(0, updatedUser.getBalance().compareTo(BigDecimal.valueOf(800))); // 잔액 1000에서 200 차감
    }
}
