package com.jane.ecommerce.application.coupon;

import com.jane.ecommerce.application.IntegrationTest;
import com.jane.ecommerce.domain.coupon.Coupon;
import com.jane.ecommerce.domain.coupon.CouponRepository;
import com.jane.ecommerce.domain.coupon.UserCouponRepository;
import com.jane.ecommerce.domain.user.UserRepository;
import com.jane.ecommerce.interfaces.dto.coupon.ClaimRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

public class ClaimCouponUseCaseConcurrencyTest extends IntegrationTest {

    @Autowired
    private ClaimCouponUseCase claimCouponUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    private Coupon testCoupon;

    @BeforeEach
    void setup() {
        // 쿠폰 데이터 생성
        testCoupon = Coupon.create("DISCOUNT10", 10L, LocalDateTime.now().plusDays(1), 10);
        couponRepository.save(testCoupon);
    }

    @Test
    void testConcurrentCouponClaims() throws InterruptedException {
        // Given: 클라이언트 요청 데이터 준비
        Long userId = 1L;
        Long couponId = testCoupon.getId();
        ClaimRequest claimRequest = new ClaimRequest(userId.toString(), couponId.toString());

        // 동시성 테스트를 위한 CountDownLatch 설정
        int numberOfThreads = 20;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // When: 여러 스레드에서 동시에 쿠폰 발급 시도
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    claimCouponUseCase.execute(claimRequest);
                } finally {
                    latch.countDown();  // 모든 스레드가 완료되면 카운트다운
                }
            });
        }

        // 모든 스레드가 작업을 마칠 때까지 기다림
        latch.await();

        // Then: 결과 검증
        Coupon updatedCoupon = couponRepository.findById(testCoupon.getId()).orElseThrow();

        // 발급 수량이 0보다 크거나 같은지 확인
        assertThat(updatedCoupon.getQuantity()).isGreaterThanOrEqualTo(0);

        // 발급된 쿠폰의 개수가 요청한 횟수만큼 맞는지 확인
        assertThat(userCouponRepository.findAll()).hasSizeLessThanOrEqualTo(10);  // 최대 10개까지만 발급 가능
    }
}
