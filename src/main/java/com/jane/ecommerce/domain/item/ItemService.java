package com.jane.ecommerce.domain.item;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderItem;
import com.jane.ecommerce.domain.order.OrderRepository;
import com.jane.ecommerce.interfaces.dto.item.TopItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    // 상품 목록 페이징 조회
    public Page<Item> getItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    // 특정 상품 조회
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ String.valueOf(itemId) }));
    }

    @Transactional
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    // 상위 상품 조회
    public List<TopItemResponse> getTopItems() {
        // 최근 3일간의 날짜 계산
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        // 최근 3일간의 주문 목록 조회
        List<Order> orders = orderRepository.findOrdersByCreatedAtAfter(threeDaysAgo);

        // 판매된 상품 목록과 판매 수량을 집계하기 위한 Map
        Map<Long, Integer> itemSales = new HashMap<>();

        // 주문 아이템에서 상품과 수량을 추출하여 집계
        for (Order order : orders) {
            if ("COMPLETED".equals(order.getStatus())) { // 완료된 주문만 고려
                for (OrderItem orderItem : order.getOrderItems()) {
                    Long itemId = orderItem.getItem().getId();
                    itemSales.put(itemId, itemSales.getOrDefault(itemId, 0) + orderItem.getQuantity());
                }
            }
        }

        // 상품 ID 목록을 기반으로 상위 5개 상품을 조회
        List<Long> topItemIds = itemSales.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())  // 판매 수량 기준 내림차순 정렬
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        // 상위 5개 상품 정보를 조회
        return topItemIds.stream()
                .map(itemId -> {
                    Item item = itemRepository.findById(itemId).orElseThrow();
                    return new TopItemResponse(item.getId().toString(), item.getName(), itemSales.get(itemId));
                })
                .collect(Collectors.toList());
    }
}
