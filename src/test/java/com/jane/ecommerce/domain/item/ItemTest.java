package com.jane.ecommerce.domain.item;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemTest {

    // 재고 감소 성공
    @Test
    void testDecreaseStock_Success() {
        // given
        Item item = Item.create("Item A", BigDecimal.ONE, 10); // 초기 재고 10

        // when
        item.decreaseStock(5); // 5 감소

        // then
        assertEquals(5, item.getStock());
    }

    @Test
    void decreaseStock_insufficientStock() {
        // given
        Item item = Item.of(1L,"Item A", BigDecimal.ONE, 3); // 초기 재고 3

        // when & then
        BaseCustomException exception = assertThrows(
                BaseCustomException.class,
                () -> item.decreaseStock(5)
        );

        // 예외 메시지 확인
        assertEquals(BaseErrorCode.INSUFFICIENT_STOCK, exception.getBaseErrorCode());
    }

    // 재고 복구 성공
    @Test
    void testRestoreStock_Success() {
        // given
        Item item = Item.create("Item A", BigDecimal.ONE, 10); // 초기 재고 10

        // when
        item.restoreStock(5); // 5 복구

        // then
        assertEquals(15, item.getStock()); // 재고가 15로 증가해야 함
    }

    // 재고 복구 예외 (음수로 복구 시도)
    @Test
    void restoreStock_negativeQuantity() {
        // given
        Item item = Item.create("Item A", BigDecimal.ONE, 10); // 초기 재고 10

        // when & then
        BaseCustomException exception = assertThrows(
                BaseCustomException.class,
                () -> item.restoreStock(-5) // 음수 복구 시도
        );

        // 예외 메시지 확인
        assertEquals(BaseErrorCode.INVALID_PARAMETER, exception.getBaseErrorCode());
    }
}
