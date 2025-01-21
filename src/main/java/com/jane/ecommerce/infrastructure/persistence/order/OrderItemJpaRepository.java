package com.jane.ecommerce.infrastructure.persistence.order;

import com.jane.ecommerce.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
}
