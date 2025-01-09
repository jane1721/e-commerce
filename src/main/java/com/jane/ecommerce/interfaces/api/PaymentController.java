package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.application.payment.PaymentUseCase;
import com.jane.ecommerce.base.dto.response.BaseResponseContent;
import com.jane.ecommerce.interfaces.dto.payment.PaymentCreateResponse;
import com.jane.ecommerce.interfaces.dto.payment.PaymentRequest;
import com.jane.ecommerce.interfaces.dto.payment.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment API", description = "결제 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    // 결제 요청
    @Operation(summary = "결제 요청", description = "주문에 대한 결제를 요청합니다.")
    @Parameter(name = "paymentRequest", description = "결제 요청 정보", required = true)
    @PostMapping
    public ResponseEntity<BaseResponseContent> processPayment(@RequestBody PaymentRequest paymentRequest) {

        PaymentCreateResponse response = paymentUseCase.processPayment(paymentRequest);

        BaseResponseContent responseContent = new BaseResponseContent(response);

        if (response.getStatus().equals("INITIATED")) {
            responseContent.setMessage("결제 성공하였습니다.");
        } else {
            responseContent.setMessage("결제 실패하였습니다.");
        }

        return ResponseEntity.ok(responseContent);
    }

    // 결제 상태 조회
    @Operation(summary = "결제 상태 조회", description = "결제 상태를 조회합니다.")
    @Parameter(name = "id", description = "결제 ID", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseContent> getPaymentStatus(@PathVariable String id) {

        PaymentResponse response = paymentUseCase.getPaymentStatus(id);

        return ResponseEntity.ok(new BaseResponseContent(response));
    }
}
