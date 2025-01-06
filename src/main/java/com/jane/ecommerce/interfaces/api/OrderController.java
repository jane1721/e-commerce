package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.base.dto.response.BaseResponseContent;
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
    public ResponseEntity<BaseResponseContent> createOrder(@RequestBody OrderCreateRequest orderCreateRequest) {

        OrderCreateResponse response = new OrderCreateResponse("111", "PENDING", 15000, LocalDateTime.now());

        BaseResponseContent responseContent = new BaseResponseContent(response);
        responseContent.setMessage("주문 성공하였습니다.");

        return ResponseEntity.ok(responseContent);
    }

    // 특정 주문 조회
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseContent> getOrder(@PathVariable String id) {

        List<OrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemDTO("1", 2));
        orderItems.add(new OrderItemDTO("2", 5));
        OrderResponse orderResponse = new OrderResponse("123", "CONFIRMED", orderItems, 15000, LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        BaseResponseContent responseContent = new BaseResponseContent(orderResponse);

        return ResponseEntity.ok(responseContent);
    }

    // 주문 상태 업데이트(확정)
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponseContent> updateOrderStatusConfirmed(@PathVariable String id, @RequestBody OrderUpdateRequest request) {

        OrderUpdateResponse response = new OrderUpdateResponse(id, request.getStatus(), LocalDateTime.now());
        BaseResponseContent baseResponseContent = new BaseResponseContent(response);

        if (request.getStatus().equals("CONFIRMED")) {
            baseResponseContent.setMessage("주문이 확정되었습니다.");

        } else if (request.getStatus().equals("CANCELLED")) {
            baseResponseContent.setMessage("주문이 취소되었습니다.");
        }

        return ResponseEntity.ok(baseResponseContent);
    }
}
