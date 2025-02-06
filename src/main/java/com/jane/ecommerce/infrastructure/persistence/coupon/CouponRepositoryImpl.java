package com.jane.ecommerce.infrastructure.persistence.coupon;

import com.jane.ecommerce.domain.coupon.Coupon;
import com.jane.ecommerce.domain.coupon.CouponQueueRepository;
import com.jane.ecommerce.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CouponRepositoryImpl implements CouponRepository, CouponQueueRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponQueueRedisRepository couponQueueRedisRepository;

    @Override
    public Optional<Coupon> findById(Long id) {
        return couponJpaRepository.findById(id);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Optional<Coupon> findByIdWithPessimisticLock(Long id) {
        return couponJpaRepository.findByIdWithPessimisticLock(id);
    }

    @Override
    public void validateCouponAvailability(Long couponId, int quantity) {
        couponQueueRedisRepository.validateCouponAvailability(couponId, quantity);
    }

    @Override
    public void checkDuplicateRequest(Long userId, Long couponId) {
        couponQueueRedisRepository.checkDuplicateRequest(userId, couponId);
    }

    @Override
    public void addToQueue(Long userId, Long couponId) {
        couponQueueRedisRepository.addToQueue(userId, couponId);
    }

    @Override
    public Long popFromQueue(Long couponId) {
        return couponQueueRedisRepository.popFromQueue(couponId);
    }
}
