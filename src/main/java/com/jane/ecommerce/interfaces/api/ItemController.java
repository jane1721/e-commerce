package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.base.dto.response.BaseResponseContent;
import com.jane.ecommerce.interfaces.dto.item.ItemResponse;
import com.jane.ecommerce.interfaces.dto.item.TopItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Item API", description = "상품 API")
@RestController
@RequestMapping("/api/items")
public class ItemController {

    // 상품 목록 조회
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponseContent> getItems() {

        List<ItemResponse> items = new ArrayList<>();
        items.add(new ItemResponse("1", "Item A", 5000, 10));
        items.add(new ItemResponse("2", "Item B", 10000, 5));

        return ResponseEntity.ok(new BaseResponseContent(items));
    }

    // 상위 상품 조회
    @Operation(summary = "상위 상품 조회", description = "최근 3일간 가장 많이 팔린 상위 5개 상품 정보를 조회합니다.")
    @GetMapping("/top")
    public ResponseEntity<BaseResponseContent> getTopItems() {

        List<TopItemResponse> items = new ArrayList<>();
        items.add(new TopItemResponse("3", "Product A", 100));
        items.add(new TopItemResponse("5", "Product B", 80));
        items.add(new TopItemResponse("6", "Product C", 75));
        items.add(new TopItemResponse("7", "Product E", 50));
        items.add(new TopItemResponse("1", "Product D", 45));

        return ResponseEntity.ok(new BaseResponseContent(items));
    }
}
