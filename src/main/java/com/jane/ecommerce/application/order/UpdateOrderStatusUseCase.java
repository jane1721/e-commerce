package com.jane.ecommerce.application.order;

import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderService;
import com.jane.ecommerce.interfaces.dto.order.OrderUpdateRequest;
import com.jane.ecommerce.interfaces.dto.order.OrderUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UpdateOrderStatusUseCase {

    private final OrderService orderService;

    @Transactional
    public OrderUpdateResponse execute(String id, OrderUpdateRequest request) {
        Order order = orderService.updateOrderStatus(Long.valueOf(id), request.getStatus());

        return new OrderUpdateResponse(
                order.getId().toString(),
                order.getStatus(),
                order.getUpdatedAt()
        );
    }
}
