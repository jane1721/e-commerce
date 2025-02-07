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
import com.jane.ecommerce.domain.user.UserService;
import com.jane.ecommerce.interfaces.dto.payment.PaymentRequest;
import com.jane.ecommerce.interfaces.dto.payment.PaymentCreateResponse;
import com.jane.ecommerce.interfaces.dto.user.ChargeRequest;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
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
    private UserService userService;

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

        PaymentRequest paymentRequest = new PaymentRequest(order.getId(), user.getId(), "CREDIT_CARD");

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

    /**
     * 테스트 시나리오
     * 1. 3개의 주문 생성 (각각 200원).
     * 2. 5번의 결제 요청 (3개의 주문 중 랜덤하게 결제 시도).
     * 3. 5번의 충전 요청 (100원씩 충전).
     */
    @Test
    @DisplayName("여러 주문 결제와 여러 번 충전이 동시에 발생할 때, 유저 잔액은 정확히 갱신되어야 한다.")
    void testConcurrentMultiplePaymentsAndCharges() throws InterruptedException {

        // given
        // 테스트용 유저 생성 (초기 잔액 1000원)
        User user = userRepository.save(User.create("testUser@example.com", "password", BigDecimal.valueOf(1000)));

        // 상품 생성 (가격 100원, 재고 50개)
        Item item = itemRepository.save(Item.create("Test Item", BigDecimal.valueOf(100), 50));

        // 3 개의 주문 생성 (각 주문마다 2개씩 아이템 구매 = 주문당 200원)
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = Order.create(user, null, BigDecimal.valueOf(200), BigDecimal.valueOf(200), OrderStatus.PENDING); // user 가 200원을 결제하는 주문 생성, 주문 상태는 PENDING
            order.addOrderItem(OrderItem.create(item, 2)); // 상품 2개를 주문에 추가
            orders.add(orderRepository.save(order));
        }

        // 결제 요청 리스트 생성 (각 주문에 대한 결제 요청)
        List<PaymentRequest> paymentRequests = orders.stream()
            .map(order -> new PaymentRequest(user.getId(), order.getId(), "CREDIT_CARD"))
            .toList();

        // 충전 요청 리스트 생성 (3번의 충전 요청, 각 200원씩 충전)
        List<ChargeRequest> chargeRequests = IntStream.range(0, 3)
            .mapToObj(i -> new ChargeRequest(user.getId(), BigDecimal.valueOf(200)))
            .toList();

        // 동시성 테스트 준비 (총 6개의 요청: 3개 결제 + 3개 충전)
        int totalRequests = paymentRequests.size() + chargeRequests.size();
        ExecutorService executor = Executors.newFixedThreadPool(totalRequests); // 스레드 풀 생성
        List<CompletableFuture<Void>> futures = new ArrayList<>(); // 비동기 작업을 저장

        // 결제 요청 실행
        for (PaymentRequest paymentRequest : paymentRequests) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    processPaymentUseCase.execute(paymentRequest);
                } catch (Exception e) {
                    // 중복 결제나 예외 발생 시 무시 (동시성 제어)
                    e.printStackTrace();
                }
            }, executor));
        }

        // 충전 요청 실행
        for (ChargeRequest chargeRequest : chargeRequests) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    userService.chargeBalance(chargeRequest);
                } catch (Exception e) {
                    // 중복 충전이나 예외 발생 시 무시 (동시성 제어)
                    e.printStackTrace();
                }
            }, executor));
        }

        // 모든 요청 완료 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // then
        // 모든 결제와 충전 완료 후 검증

        // 1. 결제가 3건 정상 처리되었는지 확인
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(3);

        // 2. 모든 주문이 완료 상태인지 확인
        orders.forEach(order -> {
            Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
            assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        });

        // 3. 최종 잔액 확인
        // 초기 잔액 1000원 - 결제 600원(3*200원) + 충전 600원(3*200원) = 최종 잔액 1000원
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }
}
