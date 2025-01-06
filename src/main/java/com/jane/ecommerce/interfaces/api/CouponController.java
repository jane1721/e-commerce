package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.base.dto.response.BaseResponseContent;
import com.jane.ecommerce.interfaces.dto.coupon.ClaimRequest;
import com.jane.ecommerce.interfaces.dto.coupon.ClaimResponse;
import com.jane.ecommerce.interfaces.dto.coupon.CouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    // 쿠폰 발급
    @PostMapping("/claim")
    public ResponseEntity<BaseResponseContent> claimCoupon(@RequestBody ClaimRequest request) {

        BaseResponseContent responseContent = new BaseResponseContent(new ClaimResponse("DISCOUNT-10"));
        responseContent.setMessage("쿠폰 발급 성공하였습니다.");

        return ResponseEntity.ok(responseContent);
    }

    // 보유 쿠폰 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponseContent> getCoupons(@PathVariable String userId) {

        List<CouponResponse> coupons = new ArrayList<>();
        coupons.add(new CouponResponse("DISCOUNT-10", 10, LocalDateTime.of(2025, 3, 31, 23, 59, 59), false));
        coupons.add(new CouponResponse("DISCOUNT-20", 20, LocalDateTime.of(2025, 1, 31, 23, 59, 59), false));

        return ResponseEntity.ok(new BaseResponseContent(coupons));
    }
}
