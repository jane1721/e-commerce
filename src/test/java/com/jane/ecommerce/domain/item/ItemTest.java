package com.jane.ecommerce.domain.item;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemTest {

    // 재고 감소 성공
    @Test
    void testDecreaseStock_Success() {
        // given
        Item item = new Item();
        item.setStock(10); // 초기 재고 10

        // when
        item.decreaseStock(5); // 5 감소

        // then
        assertEquals(5, item.getStock());
    }

    @Test
    void decreaseStock_insufficientStock() {
        // given
        Item item = new Item();
        item.setId(1L);
        item.setStock(3); // 초기 재고 3

        // when & then
        BaseCustomException exception = assertThrows(
                BaseCustomException.class,
                () -> item.decreaseStock(5)
        );

        // 예외 메시지 확인
        assertEquals(BaseErrorCode.INSUFFICIENT_STOCK, exception.getBaseErrorCode());
    }
}
