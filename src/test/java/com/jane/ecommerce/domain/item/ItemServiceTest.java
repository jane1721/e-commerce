package com.jane.ecommerce.domain.item;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderItem;
import com.jane.ecommerce.domain.order.OrderRepository;
import com.jane.ecommerce.domain.order.OrderStatus;
import com.jane.ecommerce.interfaces.dto.item.TopItemResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ItemService itemService;

    // 상품 목록 페이징 조회 성공
    @Test
    void testGetItems_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        List<Item> allItems = Arrays.asList(
            Item.of(1L, "Test Item 1", BigDecimal.valueOf(1000L), 10),
            Item.of(2L, "Test Item 2", BigDecimal.valueOf(2000L), 20),
            Item.of(3L, "Test Item 3", BigDecimal.valueOf(3000L), 30)
        );
        // 페이지 크기만큼 제한된 데이터로 생성
        List<Item> pagedItems = allItems.subList(0, 2); // 첫 페이지 데이터
        Page<Item> mockPage = new PageImpl<>(pagedItems, pageable, allItems.size()); // 전체 크기 = 3

        when(itemRepository.findAll(pageable)).thenReturn(mockPage);

        // when
        Page<Item> result = itemService.getItems(pageable);

        // then
        assertEquals(result.getTotalElements(), 3); // 전체 데이터 개수
        assertEquals(result.getContent().size(), 2); // 페이지 크기
        assertEquals(result.getContent().get(0).getName(), "Test Item 1");
        assertEquals(result.getContent().get(1).getName(), "Test Item 2");

        verify(itemRepository, times(1)).findAll(pageable);
    }

    // 특정 상품 조회 성공
    @Test
    public void testGetItemById_Success() {
        // given
        Long itemId = 1L;
        Item item = Item.of(1L, "Test Item 1", BigDecimal.valueOf(1000L), 10);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // when
        Item result = itemService.getItemById(itemId);

        // then
        assertNotNull(result);  // 상품이 반환되어야 함
        verify(itemRepository, times(1)).findById(itemId); // findById 메서드가 한번 호출되었는지 확인
    }

    // 존재하지 않는 상품 조회 실패
    @Test
    public void testGetItemById_NotFound() {
        // given
        Long itemId = 999999L;

        // 상품이 존재하지 않는 경우 (Optional.empty() 반환)
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> itemService.getItemById(itemId));

        // then
        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode()); // 예외 코드 확인
    }

    @Test
    public void testGetTopItems() {
        // given
        // 샘플 상품 데이터 생성
        Item itemA = Item.of(1L, "Product A", BigDecimal.valueOf(1000L), 100);
        Item itemB = Item.of(2L, "Product B", BigDecimal.valueOf(1500L), 200);

        // 샘플 주문 데이터 생성
        OrderItem orderItemA = OrderItem.of(1L, null, itemA, 10);
        OrderItem orderItemB = OrderItem.of(2L, null, itemB, 5);
        Order order = Order.of(1L, null, BigDecimal.valueOf(10000L), BigDecimal.valueOf(9500L), OrderStatus.COMPLETED);
        order.addOrderItem(orderItemA);
        order.addOrderItem(orderItemB);

        // 주문 목록 준비
        List<Order> orders = new ArrayList<>();
        orders.add(order);

        // Repository 모킹
        when(orderRepository.findOrdersByCreatedAtAfter(any(LocalDateTime.class))).thenReturn(orders);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemA));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(itemB));

        // when
        // 실제 서비스 메서드 호출
        List<TopItemResponse> topItems = itemService.getTopItems();

        // then
        // 반환된 결과 검증
        assertEquals(2, topItems.size());  // 반환되는 상품의 개수는 2개
        assertEquals("Product A", topItems.get(0).getName());  // 상위 1위 상품은 "Product A"
        assertEquals("Product B", topItems.get(1).getName());  // 상위 2위 상품은 "Product B"
        assertEquals(10, topItems.get(0).getSoldCount());  // "Product A"의 판매 수량은 10
        assertEquals(5, topItems.get(1).getSoldCount());   // "Product B"의 판매 수량은 5
    }
}
