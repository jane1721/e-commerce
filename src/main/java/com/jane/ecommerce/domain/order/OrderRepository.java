package com.jane.ecommerce.domain.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByIdWithOrderItems(Long id);
    List<Order> findAllByCreatedAtAfterAndStatus(LocalDateTime createdAt, OrderStatus orderStatus);
}
