package com.jane.ecommerce.domain.order;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.user.User;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    // 주문 생성
    @Transactional
    public Order createOrder(User user, List<OrderItem> orderItems, UserCoupon userCoupon) {

        Order order = Order.create(user, userCoupon, BigDecimal.ZERO, BigDecimal.ZERO, OrderStatus.PENDING); // 주문 생성 시 PENDING 상태로 생성

        // 주문 상품 추가
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        // 전체 주문 금액 계산
        order.calculateTotalAmount();
        // 쿠폰 적용하여 최종 주문 금액 계산
        order.calculateFinalAmount();

        return orderRepository.save(order);
    }

    // 특정 주문 조회
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithOrderItems(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ id.toString() }));
    }

    // 주문 상태 업데이트
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, new String[]{ id.toString() }));

        order.setStatus(status);

        return orderRepository.save(order);
    }
}
