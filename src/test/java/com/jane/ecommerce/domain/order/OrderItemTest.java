package com.jane.ecommerce.domain.order;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.domain.item.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderItemTest {

    @Test
    void testCheckStock_Success() {
        // Item mock 객체 생성
        Item mockItem = mock(Item.class);
        when(mockItem.getStock()).thenReturn(10); // 재고 10개

        // Order mock 객체 생성
        Order mockOrder = mock(Order.class);

        // OrderItem 생성
        OrderItem orderItem = new OrderItem(null, mockOrder, mockItem, 5); // 주문량 5개

        // 재고 체크 시 예외가 발생하지 않는지 확인
        assertDoesNotThrow(orderItem::checkStock);
    }

    @Test
    void testCheckStockWhenOrderQuantityExceedsStock_InsufficientStock() {
        // Item mock 객체 생성
        Item mockItem = mock(Item.class);
        when(mockItem.getStock()).thenReturn(10); // 재고 10개

        // Order mock 객체 생성
        Order mockOrder = mock(Order.class);

        // OrderItem 생성
        OrderItem orderItem = new OrderItem(null, mockOrder, mockItem, 20); // 주문량 20개

        // 재고가 부족할 경우 예외가 발생하는지 확인
        BaseCustomException exception = assertThrows(
                BaseCustomException.class, orderItem::checkStock
        );

        // 예외 메시지 확인
        assertEquals(BaseErrorCode.INSUFFICIENT_STOCK, exception.getBaseErrorCode());
    }
}
