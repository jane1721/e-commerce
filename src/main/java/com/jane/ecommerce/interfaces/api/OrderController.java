package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.interfaces.dto.order.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // 주문 생성
    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody OrderCreateRequest orderCreateRequest) {

        OrderCreateResponse response = new OrderCreateResponse("success", "주문 성공하였습니다.", new OrderDTO("123", "PENDING", 15000, LocalDateTime.of(2025, 1, 1, 12, 0, 0)));

        return ResponseEntity.ok(response);
    }

    // 특정 주문 조회
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {

        List<OrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemDTO("1", 2));
        orderItems.add(new OrderItemDTO("2", 5));
        OrderResponse response = new OrderResponse("123", "CONFIRMED", orderItems, 15000, LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        return ResponseEntity.ok(response);
    }

    // 주문 상태 업데이트(확정)
    @PatchMapping("/{id}")
    public ResponseEntity<OrderUpdateResponse> updateOrderStatusConfirmed(@PathVariable String id, @RequestBody OrderUpdateRequest request) {

        OrderUpdateResponse response = new OrderUpdateResponse("success", "주문이 확정되었습니다.", new OrderStatusDTO("123", "CONFIRMED", LocalDateTime.of(2025, 1, 1, 12, 0, 0)));

        return ResponseEntity.ok(response);
    }
}
