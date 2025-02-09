package com.jane.ecommerce.domain.coupon;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.user.UserRepository;
import com.jane.ecommerce.support.aop.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponQueueRepository couponQueueRepository;

    // 대기열에 쿠폰 발급 요청 추가
    public void addCouponRequestToQueue(Long userId, Long couponId) {
        log.info("Adding coupon request queue to queue for userId: {} couponId: {}", userId, couponId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(couponId) }));

        // 쿠폰 발급 가능 여부 검증
        boolean isCouponAvailable = couponQueueRepository.isCouponAvailable(couponId, coupon.getQuantity());
        if (!isCouponAvailable) {
            throw new CustomException(ErrorCode.INSUFFICIENT_COUPON_STOCK, new String[]{ String.valueOf(couponId) });
        }

        // 중복 요청 여부 검증
        boolean isDuplicateRequest = couponQueueRepository.isDuplicateRequest(userId, couponId);
        if (isDuplicateRequest) {
            throw new CustomException(ErrorCode.DUPLICATE_COUPON_CLAIM, new String[]{ String.valueOf(userId) });
        }

        // 대기열에 추가
        couponQueueRepository.addToQueue(userId, couponId);
    }

    // 대기열에서 쿠폰 발급 처리
    @Transactional
    public void processCouponQueue(Long couponId, int claimCount) {
        log.info("Processing coupon queue for couponId: {}", couponId);

        for (int i = 0; i < claimCount; i++) {

            Long userId = couponQueueRepository.popFromQueue(couponId);

            try {
                claimCoupon(userId, couponId);  // 기존 쿠폰 발급 로직 호출
            } catch (CustomException e) {
                // 발급 실패 시 로그 처리
                log.error("쿠폰 발급 실패: userId={} couponId={}", userId, couponId);
            }
        }
    }

    // 쿠폰 발급
    @DistributedLock(key = "#couponId")
    public UserCoupon claimCoupon(Long userId, Long couponId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(userId) }));

        // 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(couponId) }));

        // 쿠폰 발급
        coupon.claim();

        couponRepository.save(coupon);

        // 사용자에게 쿠폰 발급 기록 저장
        UserCoupon userCoupon = UserCoupon.create(user, coupon, false);

        return userCouponRepository.save(userCoupon);
    }

    // 보유 쿠폰 조회 (만료된 쿠폰은 제외)
    public List<UserCoupon> getAvailableCoupons(Long userId) {

        return userCouponRepository.findAvailableCouponsByUserId(userId);
    }

    // 유저 쿠폰 단 건 조회
    public UserCoupon getUserCouponById(long userCouponId) {
        return userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ String.valueOf(userCouponId) })); // 유저가 없을 경우 예외 처리
    }
}
