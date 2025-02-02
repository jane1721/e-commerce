package com.jane.ecommerce.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jane.ecommerce.domain.coupon.Coupon;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.item.Item;
import com.jane.ecommerce.domain.user.User;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;

public class OrderTest {

    // 전체 주문 금액 계산 성공
    @Test
    void calculateTotalAmount_Success() {
        // given
        User user = User.of(1L, null, null, null);

        Item item1 = Item.create("Item A", BigDecimal.valueOf(100L), null);
        Item item2 = Item.create("Item B", BigDecimal.valueOf(200L), null);

        Order order = Order.create(user, null, BigDecimal.ZERO, BigDecimal.ZERO, OrderStatus.PENDING);

        OrderItem orderItem1 = OrderItem.create(item1, 2); // 100 (상품 가격) * 2 (수량) = 200
        OrderItem orderItem2 = OrderItem.create(item2, 1); // 200 (상품 가격) * 1 (수량) = 200

        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);

        // when
        order.calculateTotalAmount();

        // then
        assertEquals(BigDecimal.valueOf(400L), order.getTotalAmount()); // 200 + 200 = 400
    }

    // 쿠폰 없을 때 최종 주문 금액 계산 성공
    @Test
    void calculateFinalAmount_NoCoupon_Success() {
        // given
        User user = User.of(1L, null, null, null);

        Order order = Order.create(user, null, BigDecimal.valueOf(400L), BigDecimal.ZERO, OrderStatus.PENDING);

        // when
        order.calculateFinalAmount();

        // then
        assertEquals(BigDecimal.valueOf(400L), order.getFinalAmount());
    }

    // 쿠폰 있을 때 반영하여 최종 주문 금액 계산 성공
    @Test
    void calculateFinalAmount_WithCoupon_Success() {
        // given
        User user = User.of(1L, null, null, null);

        Coupon coupon = Coupon.create(null, 20L, null, 1); // 20% 할인 쿠폰
        UserCoupon userCoupon = UserCoupon.create(user, coupon, false);

        Order order = Order.create(user, userCoupon, BigDecimal.valueOf(400L), BigDecimal.ZERO, OrderStatus.PENDING);

        // when
        order.calculateFinalAmount();

        // then
        assertEquals(BigDecimal.valueOf(320L).setScale(2, RoundingMode.HALF_UP), order.getFinalAmount()); // 400 (전체 금액) * 0.8 (할인율) = 320
    }

}
