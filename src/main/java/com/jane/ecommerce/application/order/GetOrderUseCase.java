package com.jane.ecommerce.application.order;

import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderService;
import com.jane.ecommerce.interfaces.dto.order.OrderItemDTO;
import com.jane.ecommerce.interfaces.dto.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetOrderUseCase {

    private final OrderService orderService;

    public OrderResponse execute(String id) {
        Order order = orderService.getOrderById(Long.valueOf(id));

        List<OrderItemDTO> orderItems = order.getOrderItems().stream()
                .map(item -> new OrderItemDTO(item.getItem().getId().toString(), item.getQuantity()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId().toString(),
                order.getStatus(),
                orderItems,
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }
}
