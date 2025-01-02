package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.interfaces.dto.payment.PaymentCreateResponse;
import com.jane.ecommerce.interfaces.dto.payment.PaymentRequest;
import com.jane.ecommerce.interfaces.dto.payment.PaymentDTO;
import com.jane.ecommerce.interfaces.dto.payment.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    // 결제 요청
    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {

        PaymentCreateResponse response = new PaymentCreateResponse("success", "결제 성공하였습니다.", new PaymentDTO("456", "INITIATED", LocalDateTime.of(2025, 1, 1, 12, 5)));

        return ResponseEntity.ok(response);
    }

    // 결제 상태 조회
    @GetMapping("/payments/{id}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String id) {

        PaymentResponse response = new PaymentResponse("456", "SUCCESS", 15000, "CARD", LocalDateTime.of(2025, 1, 1, 12, 10, 0));

        return ResponseEntity.ok(response);
    }
}
