package com.jane.ecommerce.domain.item;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderItem;
import com.jane.ecommerce.domain.order.OrderRepository;
import com.jane.ecommerce.interfaces.dto.item.TopItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    private ItemService itemService;
    private ItemRepository itemRepository;
    private OrderRepository orderRepository;

    private List<Order> orders;  // 주문 목록 준비
    private Item itemA;
    private Item itemB;

    @BeforeEach
    void setUp() {
        // Mockito.mock() 메서드를 직접 호출하여 Mock 객체 생성
        itemRepository = mock(ItemRepository.class);
        orderRepository = mock(OrderRepository.class);
        itemService = new ItemService(itemRepository, orderRepository); // 의존성 직접 주입

        // 샘플 상품 데이터 생성
        itemA = new Item(1L, "Product A", 1000L, 100, new ArrayList<>(), new ArrayList<>());
        itemB = new Item(2L, "Product B", 1500L, 200, new ArrayList<>(), new ArrayList<>());

        // 샘플 주문 데이터 생성
        OrderItem orderItemA = new OrderItem(1L, null, itemA, 10);
        OrderItem orderItemB = new OrderItem(2L, null, itemB, 5);
        Order order = new Order(1L, null, null, 10000L, 9500L, "COMPLETED", new ArrayList<>(), null);
        order.addOrderItem(orderItemA);
        order.addOrderItem(orderItemB);

        orders = new ArrayList<>();
        orders.add(order);

        // Repository 모킹
        when(orderRepository.findOrdersByCreatedAtAfter(any(LocalDateTime.class))).thenReturn(orders);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemA));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(itemB));
    }

    // 상품 목록 페이징 조회 성공
    @Test
    void testGetItems_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        List<Item> allItems = Arrays.asList(
            new Item(1L, "Test Item 1", 1000L, 10, null, null),
            new Item(2L, "Test Item 2", 2000L, 20, null, null),
            new Item(3L, "Test Item 3", 3000L, 30, null, null)
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
        Item item = new Item(1L, "Test Item 1", 1000L, 10, null, null);
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
        BaseCustomException exception = assertThrows(BaseCustomException.class, () -> {
            itemService.getItemById(itemId);
        });

        // then
        assertEquals(BaseErrorCode.NOT_FOUND, exception.getBaseErrorCode()); // 예외 코드 확인
    }

    @Test
    public void testGetTopItems() {
        // 실제 서비스 메서드 호출
        List<TopItemResponse> topItems = itemService.getTopItems();

        // 반환된 결과 검증
        assertEquals(2, topItems.size());  // 반환되는 상품의 개수는 2개
        assertEquals("Product A", topItems.get(0).getName());  // 상위 1위 상품은 "Product A"
        assertEquals("Product B", topItems.get(1).getName());  // 상위 2위 상품은 "Product B"
        assertEquals(10, topItems.get(0).getSoldCount());  // "Product A"의 판매 수량은 10
        assertEquals(5, topItems.get(1).getSoldCount());   // "Product B"의 판매 수량은 5
    }
}
