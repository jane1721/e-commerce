package com.jane.ecommerce.domain.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
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
}
