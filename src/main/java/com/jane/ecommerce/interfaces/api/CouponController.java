package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.application.coupon.AddCouponRequestToQueueUseCase;
import com.jane.ecommerce.application.coupon.ClaimCouponUseCase;
import com.jane.ecommerce.application.coupon.GetCouponsByUserIdUseCase;
import com.jane.ecommerce.interfaces.dto.response.BaseResponseContent;
import com.jane.ecommerce.interfaces.dto.coupon.ClaimRequest;
import com.jane.ecommerce.interfaces.dto.coupon.ClaimResponse;
import com.jane.ecommerce.interfaces.dto.coupon.CouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Coupon API", description = "쿠폰 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final ClaimCouponUseCase claimCouponUseCase;
    private final AddCouponRequestToQueueUseCase addCouponRequestToQueueUseCase;
    private final GetCouponsByUserIdUseCase getCouponsByUserIdUseCase;

    // 쿠폰 발급
    @Operation(summary = "쿠폰 발급", description = "사용자에게 쿠폰을 발급합니다.")
    @Parameter(name = "claimRequest", description = "쿠폰 발급 요청 정보", required = true)
    @PostMapping("/claim")
    public ResponseEntity<BaseResponseContent> claimCoupon(@RequestBody ClaimRequest claimRequest) {

        ClaimResponse claimResponse = claimCouponUseCase.execute(claimRequest);

        return ResponseEntity.ok(new BaseResponseContent(claimResponse));
    }

    // 쿠폰 발급 요청을 대기열에 추가
    @Operation(summary = "쿠폰 발급 요청을 대기열에 추가", description = "쿠폰 발급 요청을 대기열에 추가합니다.")
    @Parameter(name = "claimRequest", description = "쿠폰 발급 요청 정보", required = true)
    @PostMapping("/add-queue")
    public ResponseEntity<BaseResponseContent> addCouponRequestToQueue(@RequestBody ClaimRequest claimRequest) {

        ClaimResponse claimResponse = addCouponRequestToQueueUseCase.execute(claimRequest);

        return ResponseEntity.ok(new BaseResponseContent(claimResponse));
    }


    // 보유 쿠폰 조회
    @Operation(summary = "보유 쿠폰 조회", description = "사용자가 보유한 쿠폰 리스트를 조회합니다.")
    @Parameter(name = "userId", description = "사용자 ID", required = true)
    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponseContent> getCouponsByUserId(@PathVariable String userId) {

        List<CouponResponse> coupons = getCouponsByUserIdUseCase.execute(userId);

        return ResponseEntity.ok(new BaseResponseContent(coupons));
    }
}
