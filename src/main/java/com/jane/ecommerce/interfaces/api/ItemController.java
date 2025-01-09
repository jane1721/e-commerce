package com.jane.ecommerce.interfaces.api;

import com.jane.ecommerce.application.item.GetItemsUseCase;
import com.jane.ecommerce.application.item.GetTopItemsUseCase;
import com.jane.ecommerce.base.dto.response.BaseResponseContent;
import com.jane.ecommerce.base.dto.response.BaseResponsePage;
import com.jane.ecommerce.interfaces.dto.item.ItemResponse;
import com.jane.ecommerce.interfaces.dto.item.TopItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Item API", description = "상품 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final GetItemsUseCase getItemsUseCase;
    private final GetTopItemsUseCase getTopItemsUseCase;

    // 상품 목록 페이징 조회
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponsePage> getItems(
        @PageableDefault(page = 0, size = Integer.MAX_VALUE) Pageable pageable
    ) {
        Page<ItemResponse> items = getItemsUseCase.execute(pageable);

        return ResponseEntity.ok(new BaseResponsePage(items));
    }

    // 상위 상품 조회
    @Operation(summary = "상위 상품 조회", description = "최근 3일간 가장 많이 팔린 상위 5개 상품 정보를 조회합니다.")
    @GetMapping("/top")
    public ResponseEntity<BaseResponseContent> getTopItems() {

        List<TopItemResponse> items = getTopItemsUseCase.execute();

        return ResponseEntity.ok(new BaseResponseContent(items));
    }
}
