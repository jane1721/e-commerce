package com.jane.ecommerce.infrastructure.persistence.coupon;

import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.error.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository
public class CouponQueueRedisRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 쿠폰 발급 가능 여부 검증
    public void validateCouponAvailability(Long couponId, int totalQuantity) {
        String userCouponKey = "couponClaimed:" + couponId;
        Long claimedCount = redisTemplate.opsForSet().size(userCouponKey);

        if (claimedCount != null && claimedCount >= totalQuantity) {
            throw new CustomException(ErrorCode.INSUFFICIENT_COUPON_STOCK, new String[]{ String.valueOf(couponId) });
        }
    }

    // 중복 요청 검증
    public void checkDuplicateRequest(Long userId, Long couponId) {
        String userCouponKey = "couponClaimed:" + couponId;
        String userClaimKey = userId + ":" + couponId;

        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userCouponKey, userClaimKey))) {
            throw new CustomException(ErrorCode.DUPLICATE_COUPON_CLAIM, new String[]{ String.valueOf(userId) });
        }
    }

    // 대기열에 추가
    public void addToQueue(Long userId, Long couponId) {
        String couponQueueKey = "couponQueue:" + couponId;
        String userCouponKey = "couponClaimed:" + couponId;

        redisTemplate.opsForZSet().add(couponQueueKey, String.valueOf(userId), System.currentTimeMillis());
        redisTemplate.opsForSet().add(userCouponKey, userId + ":" + couponId);
    }

    // 대기열에서 사용자 꺼내기
    public Long popFromQueue(Long couponId) {
        String couponQueueKey = "couponQueue:" + couponId;
        ZSetOperations.TypedTuple<String> userTuple = redisTemplate.opsForZSet().popMin(couponQueueKey);

        if (userTuple != null) {
            return Long.valueOf(userTuple.getValue());
        }
        return null;
    }
}
