package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.interfaces.dto.response.BaseResponse;
import com.jane.ecommerce.interfaces.dto.response.BaseResponseContent;
import com.jane.ecommerce.interfaces.dto.cart.CartDeleteRequest;
import com.jane.ecommerce.interfaces.dto.cart.CartInsertRequest;
import com.jane.ecommerce.interfaces.dto.cart.CartItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Cart API", description = "장바구니 API")
@RestController
@RequestMapping("/api/carts")
public class CartController {

    // 장바구니 추가
    @Operation(summary = "장바구니 추가", description = "사용자의 장바구니에 상품을 추가합니다.")
    @Parameter(name = "cartInsertRequest", description = "장바구니에 추가할 상품 정보", required = true)
    @PostMapping
    public ResponseEntity<BaseResponse> addToCart(@RequestBody CartInsertRequest cartInsertRequest) {

        BaseResponse baseResponse = new BaseResponse();

        return ResponseEntity.ok(baseResponse);
    }

    // 장바구니 삭제
    @Operation(summary = "장바구니 삭제", description = "사용자의 장바구니에 상품을 추가합니다.")
    @Parameter(name = "cartDeleteRequest", description = "장바구니에서 삭제할 상품 정보", required = true)
    @DeleteMapping
    public ResponseEntity<BaseResponse> removeFromCart(@RequestBody CartDeleteRequest cartDeleteRequest) {

        BaseResponse baseResponse = new BaseResponse();

        return ResponseEntity.ok(baseResponse);
    }

    // 장바구니 조회
    @Operation(summary = "장바구니 조회", description = "사용자의 장바구니에 담긴 상품 리스트를 조회합니다.")
    @Parameter(name = "userId", description = "사용자 ID", required = true)
    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponseContent> getCartItems(@PathVariable String userId) {

        List<CartItemResponse> items = new ArrayList<>();
        items.add(new CartItemResponse("1", "Item A", 2, 5000));
        items.add(new CartItemResponse("2", "Item B", 6, 8000));

        BaseResponseContent responseContent = new BaseResponseContent(items);

        return ResponseEntity.ok(responseContent);
    }
}
