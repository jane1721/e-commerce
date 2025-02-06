package com.jane.ecommerce.application.item;

import com.jane.ecommerce.domain.item.ItemService;
import com.jane.ecommerce.interfaces.dto.item.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetItemsUseCase {

    private final ItemService itemService;

    // 상품 목록 조회 캐시 키
    private static final String ITEMS_CACHE_KEY = "items";

    @Cacheable(cacheNames = ITEMS_CACHE_KEY, key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ItemResponse> execute(Pageable pageable) {
        return itemService.getItems(pageable).map(ItemResponse::from);
    }
}
