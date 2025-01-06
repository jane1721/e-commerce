package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.base.dto.response.BaseResponseContent;
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
    public ResponseEntity<BaseResponseContent> chargeBalance(@RequestBody ChargeRequest request) {

        BaseResponseContent responseContent =new BaseResponseContent(new ChargeResponse( 20000));
        responseContent.setMessage("충전 요청 성공하였습니다.");

        return ResponseEntity.ok(responseContent);
    }

    // 잔액 조회
    @GetMapping("/{userId}/balance")
    public ResponseEntity<BaseResponseContent> getBalance(@PathVariable String userId) {

        BalanceResponse response = new BalanceResponse(userId, 20000);

        return ResponseEntity.ok(new BaseResponseContent(response));
    }
}
