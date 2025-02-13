package com.jane.ecommerce.infrastructure.persistence.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CouponQueueRedisRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String COUPON_QUEUE_SORTED_SET_KEY = "couponQueue:";
    private static final String COUPON_CLAIMED_SET_KEY = "couponClaimed:";

    // 쿠폰 발급 가능 여부 반환
    public boolean isCouponAvailable(Long couponId, int totalQuantity) {
        String userCouponKey = COUPON_CLAIMED_SET_KEY + couponId;
        Long claimedCount = redisTemplate.opsForSet().size(userCouponKey);

        return claimedCount < totalQuantity;
    }

    // 중복 요청 여부 반환
    public boolean isDuplicateRequest(Long userId, Long couponId) {
        String userCouponKey = COUPON_CLAIMED_SET_KEY + couponId;
        String userClaimKey = userId + ":" + couponId;

        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userCouponKey, userClaimKey));
    }

    // 대기열에 추가
    public void addToQueue(Long userId, Long couponId) {
        String couponQueueKey = COUPON_QUEUE_SORTED_SET_KEY + couponId;
        String userCouponKey = COUPON_CLAIMED_SET_KEY + couponId;

        redisTemplate.opsForZSet().add(couponQueueKey, String.valueOf(userId), System.currentTimeMillis());
        redisTemplate.opsForSet().add(userCouponKey, userId + ":" + couponId);
    }

    // 대기열에서 사용자 꺼내기
    public Long popFromQueue(Long couponId) {
        String couponQueueKey = COUPON_QUEUE_SORTED_SET_KEY + couponId;
        ZSetOperations.TypedTuple<String> userTuple = redisTemplate.opsForZSet().popMin(couponQueueKey);

        return Long.valueOf(userTuple.getValue());
    }
}
