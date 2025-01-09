package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.application.order.CreateOrderUseCase;
import com.jane.ecommerce.application.order.GetOrderUseCase;
import com.jane.ecommerce.application.order.UpdateOrderStatusUseCase;
import com.jane.ecommerce.base.dto.response.BaseResponseContent;
import com.jane.ecommerce.interfaces.dto.order.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order API", description = "주문 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    // 주문 생성
    @Operation(summary = "주문 생성", description = "주문 내역을 가지고 주문을 생성합니다.")
    @Parameter(name = "orderCreateRequest", description = "주문 생성 정보", required = true)
    @PostMapping
    public ResponseEntity<BaseResponseContent> createOrder(@RequestBody OrderCreateRequest orderCreateRequest) {

        OrderCreateResponse response = createOrderUseCase.execute(orderCreateRequest.getUserId(), orderCreateRequest.getOrderItems(), orderCreateRequest.getUserCouponId());

        BaseResponseContent responseContent = new BaseResponseContent(response);
        responseContent.setMessage("주문 성공하였습니다.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseContent);
    }

    // 특정 주문 조회
    @Operation(summary = "특정 주문 조회", description = "특정 주문을 조회합니다.")
    @Parameter(name = "id", description = "주문 ID", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseContent> getOrder(@PathVariable String id) {

        OrderResponse orderResponse = getOrderUseCase.execute(id);

        BaseResponseContent responseContent = new BaseResponseContent(orderResponse);

        return ResponseEntity.ok(responseContent);
    }

    // 주문 상태 업데이트
    @Operation(summary = "주문 상태 업데이트", description = "주문 상태를 업데이트 합니다.")
    @Parameter(name = "id", description = "주문 ID", required = true)
    @Parameter(name = "orderUpdateRequest", description = "주문 상태 업데이트 요청 정보", required = true)
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponseContent> updateOrderStatus(@PathVariable String id, @RequestBody OrderUpdateRequest orderUpdateRequest) {

        OrderUpdateResponse response = updateOrderStatusUseCase.execute(id, orderUpdateRequest);

        BaseResponseContent baseResponseContent = new BaseResponseContent(response);

        if ("CONFIRMED".equals(orderUpdateRequest.getStatus())) {
            baseResponseContent.setMessage("주문이 확정되었습니다.");
        } else if ("CANCELLED".equals(orderUpdateRequest.getStatus())) {
            baseResponseContent.setMessage("주문이 취소되었습니다.");
        }

        return ResponseEntity.ok(baseResponseContent);
    }
}
