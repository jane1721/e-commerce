package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.interfaces.dto.item.ItemResponse;
import com.jane.ecommerce.interfaces.dto.item.TopItemResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    // 상품 정보 조회
    @GetMapping
    public ResponseEntity<List<ItemResponse>> getItems() {

        List<ItemResponse> items = new ArrayList<>();
        items.add(new ItemResponse("1", "Item A", 5000, 10));
        items.add(new ItemResponse("2", "Item B", 10000, 5));

        return ResponseEntity.ok(items);
    }

    // 상위 상품 조회
    @GetMapping("/top")
    public ResponseEntity<List<TopItemResponse>> getTopItems() {

        List<TopItemResponse> items = new ArrayList<>();
        items.add(new TopItemResponse("3", "Product A", 100));
        items.add(new TopItemResponse("5", "Product B", 80));
        items.add(new TopItemResponse("6", "Product C", 70));

        return ResponseEntity.ok(items);
    }
}
