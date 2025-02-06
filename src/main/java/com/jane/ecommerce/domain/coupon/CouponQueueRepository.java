package com.jane.ecommerce.domain.coupon;

public interface CouponQueueRepository {
    void validateCouponAvailability(Long couponId, int quantity);
    void checkDuplicateRequest(Long userId, Long couponId);
    void addToQueue(Long userId, Long couponId);
    Long popFromQueue(Long couponId);
}
