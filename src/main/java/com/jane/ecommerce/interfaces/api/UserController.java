package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.interfaces.dto.user.BalanceResponse;
import com.jane.ecommerce.interfaces.dto.user.ChargeRequest;
import com.jane.ecommerce.interfaces.dto.user.ChargeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 잔액 충전
    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> chargeBalance(@RequestBody ChargeRequest request) {

        ChargeResponse response = new ChargeResponse("success", "충전 성공하였습니다.", 20000);

        return ResponseEntity.ok(response);
    }

    // 잔액 조회
    @GetMapping("/users/{userId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String userId) {

        BalanceResponse response = new BalanceResponse(userId, 20000);

        return ResponseEntity.ok(response);
    }

}
