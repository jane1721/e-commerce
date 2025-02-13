package com.jane.ecommerce.infrastructure.persistence.order;

import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.jane.ecommerce.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id);
    }

    @Override
    public Optional<Order> findByIdWithOrderItems(Long id) {
        return orderJpaRepository.findByIdWithOrderItems(id);
    }

    @Override
    public List<Order> findAllByCreatedAtAfterAndStatus(LocalDateTime createdAt, OrderStatus orderStatus) {
        return orderJpaRepository.findAllByCreatedAtAfterAndStatus(createdAt, orderStatus);
    }
}
