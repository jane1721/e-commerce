package com.jane.ecommerce.infrastructure.persistence.order;

import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithOrderItems(Long id);

    List<Order> findAllByCreatedAtAfterAndStatus(LocalDateTime createdAt, OrderStatus orderStatus);
}
