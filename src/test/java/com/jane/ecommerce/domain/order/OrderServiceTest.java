package com.jane.ecommerce.domain.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.domain.coupon.Coupon;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.item.Item;
import com.jane.ecommerce.domain.user.User;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    // 쿠폰 적용된 주문 생성 성공
    @Test
    public void testCreateOrderWithCoupon_Success() {
        // given
        Item item = new Item();
        item.setPrice(100L); // 상품 가격 100

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(2); // 수량 2개

        Coupon coupon = new Coupon();
        coupon.setDiscountPercent(20L); // 20% 할인

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setCoupon(coupon);

        List<OrderItem> orderItems = List.of(orderItem);

        User user = new User();
        user.setId(1L);

        // 전체 금액 100 * 2 = 200
        // 최종 금액은 20% 할인된 금액 200 * 0.8 = 160

        // save 에 전달된 Order 객체를 반환
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order createdOrder = orderService.createOrder(user, orderItems, userCoupon);

        // then
        assertNotNull(createdOrder);
        assertEquals(200L, createdOrder.getTotalAmount()); // 총 금액은 200
        assertEquals(160L, createdOrder.getFinalAmount()); // 최종 금액은 160 (할인 적용)
        assertEquals("PENDING", createdOrder.getStatus()); // 상태는 PENDING 이어야 한다
        verify(orderRepository, times(1)).save(any(Order.class)); // save 가 한 번 호출됐는지 확인
    }

    // 쿠폰 적용 안 된 주문 생성 성공
    @Test
    public void testCreateOrderWithoutCoupon_Success() {
        // given
        Item item = new Item();
        item.setPrice(100L); // 상품 가격 100

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(2); // 수량 2개

        Coupon coupon = new Coupon();
        coupon.setDiscountPercent(20L); // 20% 할인

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setCoupon(coupon);

        List<OrderItem> orderItems = List.of(orderItem);

        User user = new User();
        user.setId(1L);

        // 전체 금액 100 * 2 = 200
        // 쿠폰이 없으므로 최종 금액은 전체 금액과 동일

        // save 에 전달된 Order 객체를 반환
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order createdOrder = orderService.createOrder(user, orderItems, null); // 쿠폰 없이 호출

        // then
        assertNotNull(createdOrder);
        assertEquals(200L, createdOrder.getTotalAmount()); // 총 금액은 200
        assertEquals(200L, createdOrder.getFinalAmount()); // 최종 금액은 200 (할인 없음)
        assertEquals("PENDING", createdOrder.getStatus()); // 상태는 PENDING 이어야 한다
        verify(orderRepository, times(1)).save(any(Order.class)); // save 가 한 번 호출됐는지 확인
    }

    // 특정 주문 조회 성공
    @Test
    public void testGetOrderById_Success() {
        // given
        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setStatus("CONFIRMED");
        // 다른 속성들 설정
        when(orderRepository.findByIdWithOrderItems(orderId)).thenReturn(Optional.of(mockOrder));

        // when
        Order order = orderService.getOrderById(orderId);

        // then
        assertNotNull(order);
        assertEquals(orderId, order.getId());
        assertEquals("CONFIRMED", order.getStatus());
        verify(orderRepository, times(1)).findByIdWithOrderItems(orderId);  // 메서드 호출 횟수 확인
    }

    // 존재하지 않는 주문 조회 실패
    @Test
    public void testGetOrderById_NotFound() {
        // given
        Long orderId = 1L;
        when(orderRepository.findByIdWithOrderItems(orderId)).thenReturn(Optional.empty());

        // when & then
        BaseCustomException exception = assertThrows(BaseCustomException.class, () -> {
            orderService.getOrderById(orderId);
        });

        assertEquals(BaseErrorCode.NOT_FOUND, exception.getBaseErrorCode());
        verify(orderRepository, times(1)).findByIdWithOrderItems(orderId);  // 메서드 호출 횟수 확인
    }

    // 주문 상태 업데이트 성공
    @Test
    void testUpdateOrderStatus_Success() {
        // given
        Long orderId = 1L;
        String newStatus = "CONFIRMED";

        // 주문 mock 객체 생성
        Order order = new Order();
        order.setId(orderId);
        order.setStatus("PENDING");

        // repository 가 반환할 mockOrder 설정
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // 주문 상태가 업데이트되었을 때 반환할 값을 설정
        when(orderRepository.save(order)).thenReturn(order);

        // when
        Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);

        // then
        assertEquals(newStatus, updatedOrder.getStatus());
        verify(orderRepository, times(1)).save(order); // save 메서드가 한번 호출되었는지 확인
    }

    // 존재하지 않는 주문 상태 업데이트 실패
    @Test
    void testUpdateOrderStatus_NotFound() {
        // given
        Long orderId = 1L;
        String newStatus = "CONFIRMED";

        // 주문이 존재하지 않는 경우 (Optional.empty() 반환)
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when, then
        BaseCustomException exception = assertThrows(BaseCustomException.class, () -> {
            orderService.updateOrderStatus(orderId, newStatus);
        });

        assertEquals(BaseErrorCode.NOT_FOUND, exception.getBaseErrorCode()); // 예외 코드 확인
    }
}
