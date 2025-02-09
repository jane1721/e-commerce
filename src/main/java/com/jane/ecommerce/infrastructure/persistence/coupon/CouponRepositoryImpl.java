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
    public boolean isCouponAvailable(Long couponId, int quantity) {
        return couponQueueRedisRepository.isCouponAvailable(couponId, quantity);
    }

    @Override
    public boolean isDuplicateRequest(Long userId, Long couponId) {
        return couponQueueRedisRepository.isDuplicateRequest(userId, couponId);
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
