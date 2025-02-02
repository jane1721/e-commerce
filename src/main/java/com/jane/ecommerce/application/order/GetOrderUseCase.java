package com.jane.ecommerce.application.order;

import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderService;
import com.jane.ecommerce.interfaces.dto.order.OrderItemDTO;
import com.jane.ecommerce.interfaces.dto.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GetOrderUseCase {

    private final OrderService orderService;

    @Transactional(readOnly = true)
    public OrderResponse execute(Long id) {
        Order order = orderService.getOrderById(id);

        List<OrderItemDTO> orderItems = order.getOrderItems().stream()
                .map(item -> new OrderItemDTO(item.getItem().getId(), item.getQuantity()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                orderItems,
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }
}
