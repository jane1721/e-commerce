package com.jane.ecommerce.application.order;

import com.jane.ecommerce.application.IntegrationTest;
import com.jane.ecommerce.domain.coupon.Coupon;
import com.jane.ecommerce.domain.coupon.CouponRepository;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.coupon.UserCouponRepository;
import com.jane.ecommerce.domain.item.Item;
import com.jane.ecommerce.domain.item.ItemRepository;
import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.user.UserRepository;
import com.jane.ecommerce.interfaces.dto.order.OrderItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class CreateOrderUseCaseConcurrencyTest extends IntegrationTest {

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Test
    @DisplayName("10명이 동시에 주문하더라도, 보유 재고보다 총 주문량이 적으면 모든 주문은 성공한다.")
    void testConcurrentCreateOrder() {
        // given: 테스트용 유저, 상품, 쿠폰 생성
        User user = userRepository.save(User.create("testUser@example.com", "password", BigDecimal.valueOf(100)));
        Item item = itemRepository.save(Item.create("Test Item", BigDecimal.valueOf(100), 50)); // 이름, 가격, 재고
        Coupon coupon = couponRepository.save(Coupon.create("Discount-5", 5L, LocalDateTime.of(2025, 3, 31, 23, 59, 59), 10));
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.create(user, coupon, false));

        // OrderItemDTO 생성
        OrderItemDTO orderItemDTO = new OrderItemDTO(item.getId().toString(), 2); // 아이템 ID, 주문 수량

        // 동시성 테스트 준비
        int concurrentRequests = 10;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        CompletableFuture<?>[] futures = new CompletableFuture[concurrentRequests];

        // when: 동시 요청 실행
        for (int i = 0; i < concurrentRequests; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                var response = createOrderUseCase.execute(
                        user.getId().toString(),
                        List.of(orderItemDTO),
                        userCoupon.getId().toString()
                );
                assertThat(response).isNotNull();
                assertThat(response.getId()).isNotBlank();
            }, executor);
        }

        CompletableFuture.allOf(futures).join();
        executor.shutdown();

        // then: 동시성 테스트 후 재고 확인
        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getStock()).isEqualTo(50 - (2 * concurrentRequests)); // 총 주문량 차감
    }
}
