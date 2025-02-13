package com.jane.ecommerce.domain.coupon;

public interface CouponQueueRepository {
    boolean isCouponAvailable(Long couponId, int quantity);
    boolean isDuplicateRequest(Long userId, Long couponId);
    void addToQueue(Long userId, Long couponId);
    Long popFromQueue(Long couponId);
}
