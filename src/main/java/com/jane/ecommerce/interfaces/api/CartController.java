package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.interfaces.dto.cart.CartDeleteRequest;
import com.jane.ecommerce.interfaces.dto.cart.CartInsertRequest;
import com.jane.ecommerce.interfaces.dto.cart.CartItemResponse;
import com.jane.ecommerce.interfaces.dto.cart.CartStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    // 장바구니 추가
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody CartInsertRequest request) {

        return ResponseEntity.ok(new CartStatusResponse("success", "장바구니 추가 성공하였습니다."));
    }

    @DeleteMapping("/carts")
    public ResponseEntity<?> removeFromCart(@RequestBody CartDeleteRequest request) {


        return ResponseEntity.ok(new CartStatusResponse("success", "장바구니 상품 삭제 성공하였습니다."));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable String userId) {

        List<CartItemResponse> items = new ArrayList<>();
        items.add(new CartItemResponse("1", "Item A", 2, 5000));

        return ResponseEntity.ok(items);
    }
}
