package com.jane.ecommerce.infrastructure.persistence.coupon;

public interface CouponQueueRepository {
    void validateCouponAvailability(Long couponId, Long totalQuantity);
    void checkDuplicateRequest(Long userId, Long couponId);
    void addToQueue(Long userId, Long couponId);
    Long popFromQueue(Long couponId);
}
