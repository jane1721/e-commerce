package com.jane.ecommerce.application.item;

import com.jane.ecommerce.domain.item.ItemService;
import com.jane.ecommerce.interfaces.dto.item.TopItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetTopItemsUseCase {

    private final ItemService itemService;
    private final RedisCacheManager redisCacheManager;

    // 상위 상품 조회 캐시 키
    private static final String TOP_ITEMS_CACHE_KEY = "topItems";

    @Cacheable(cacheNames = TOP_ITEMS_CACHE_KEY, key = "'topItems'")
    public List<TopItemResponse> execute() {

        return itemService.getTopItems();
    }

    // 상위 상품을 조회하는 메소드 (캐시 갱신)
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void updateTopItemsCache() {

        List<TopItemResponse> topItems = itemService.getTopItems();
        redisCacheManager.getCache(TOP_ITEMS_CACHE_KEY).put("topItems", topItems);
    }
}
