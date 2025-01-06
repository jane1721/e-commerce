package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.base.dto.response.BaseResponse;
import com.jane.ecommerce.base.dto.response.BaseResponseContent;
import com.jane.ecommerce.interfaces.dto.cart.CartDeleteRequest;
import com.jane.ecommerce.interfaces.dto.cart.CartInsertRequest;
import com.jane.ecommerce.interfaces.dto.cart.CartItemResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    // 장바구니 추가
    @PostMapping
    public ResponseEntity<BaseResponse> addToCart(@RequestBody CartInsertRequest request) {

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage("장바구니 추가 성공하였습니다.");

        return ResponseEntity.ok(baseResponse);
    }

    // 장바구니 삭제
    @DeleteMapping
    public ResponseEntity<BaseResponse> removeFromCart(@RequestBody CartDeleteRequest request) {

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage("장바구니 상품 삭제 성공하였습니다.");

        return ResponseEntity.ok(baseResponse);
    }

    // 장바구니 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponseContent> getCartItems(@PathVariable String userId) {

        List<CartItemResponse> items = new ArrayList<>();
        items.add(new CartItemResponse("1", "Item A", 2, 5000));
        items.add(new CartItemResponse("2", "Item B", 6, 8000));

        BaseResponseContent responseContent = new BaseResponseContent(items);

        return ResponseEntity.ok(responseContent);
    }
}
