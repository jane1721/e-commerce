package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.base.dto.response.BaseResponseContent;
import com.jane.ecommerce.interfaces.dto.payment.PaymentCreateResponse;
import com.jane.ecommerce.interfaces.dto.payment.PaymentRequest;
import com.jane.ecommerce.interfaces.dto.payment.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Payment API", description = "결제 API")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    // 결제 요청
    @Operation(summary = "결제 요청", description = "주문에 대한 결제를 요청합니다.")
    @Parameter(name = "paymentRequest", description = "결제 요청 정보", required = true)
    @PostMapping
    public ResponseEntity<BaseResponseContent> processPayment(@RequestBody PaymentRequest paymentRequest) {

        PaymentCreateResponse response = new PaymentCreateResponse("456", "INITIATED", LocalDateTime.of(2025, 1, 1, 12, 5));

        BaseResponseContent responseContent = new BaseResponseContent(response);
        responseContent.setMessage("결제 성공하였습니다.");

        return ResponseEntity.ok(responseContent);
    }

    // 결제 상태 조회
    @Operation(summary = "결제 상태 조회", description = "결제 상태를 조회합니다.")
    @Parameter(name = "id", description = "결제 ID", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseContent> getPaymentStatus(@PathVariable String id) {

        PaymentResponse response = new PaymentResponse("456", "SUCCESS", 15000, "CARD", LocalDateTime.of(2025, 1, 1, 12, 10, 0));

        return ResponseEntity.ok(new BaseResponseContent(response));
    }
}
