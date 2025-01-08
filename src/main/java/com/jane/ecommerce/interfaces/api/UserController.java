package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.base.dto.response.BaseResponseContent;
import com.jane.ecommerce.domain.user.UserService;
import com.jane.ecommerce.interfaces.dto.user.BalanceResponse;
import com.jane.ecommerce.interfaces.dto.user.ChargeRequest;
import com.jane.ecommerce.interfaces.dto.user.ChargeResponse;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 잔액 충전
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    @Parameter(name = "chargeRequest", description = "충전 요청 정보", required = true)
    @PostMapping("/charge")
    public ResponseEntity<BaseResponseContent> chargeBalance(@RequestBody ChargeRequest request) {

        ChargeResponse response = userService.chargeBalance(request);

        BaseResponseContent responseContent = new BaseResponseContent(response);
        responseContent.setMessage("충전 요청 성공하였습니다.");

        return ResponseEntity.ok(responseContent);
    }

    // 잔액 조회
    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    @Parameter(name = "id", description = "사용자 ID", required = true)
    @GetMapping("/{id}/balance")
    public ResponseEntity<BaseResponseContent> getBalance(@PathVariable String id) {

        BalanceResponse response = userService.getBalance(id);

        return ResponseEntity.ok(new BaseResponseContent(response));
    }
}
