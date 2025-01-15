package com.jane.ecommerce.domain.order;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
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

        // 전체 가격 계산 로직
        BigDecimal totalAmount = orderItems.stream()
                .map(orderItem -> orderItem.getItem().getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 쿠폰 적용하여 최종 가격 계산 로직
        BigDecimal finalAmount = totalAmount;
        if (userCoupon != null && userCoupon.getCoupon() != null) {
            BigDecimal discountPercent = BigDecimal.valueOf(userCoupon.getCoupon().getDiscountPercent());
            BigDecimal discountFactor = BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100)));
            finalAmount = totalAmount.multiply(discountFactor); // 할인 비율을 적용하여 최종 금액 계산
        }

        Order order = Order.create(user, userCoupon, totalAmount, finalAmount, "PENDING"); // 주문 생성 시 PENDING 상태로 생성

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return orderRepository.save(order);
    }

    // 특정 주문 조회
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithOrderItems(id)
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ id.toString() }));
    }

    // 주문 상태 업데이트
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ id.toString() }));

        order.setStatus(status);

        return orderRepository.save(order);
    }
}
