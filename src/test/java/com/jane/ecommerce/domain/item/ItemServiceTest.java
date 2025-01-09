package com.jane.ecommerce.domain.item;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
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

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class); // Mockito.mock() 메서드를 직접 호출하여 Mock 객체 생성
        itemService = new ItemService(itemRepository); // 의존성 직접 주입
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
}
