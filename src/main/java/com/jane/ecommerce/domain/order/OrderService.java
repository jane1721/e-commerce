package com.jane.ecommerce.domain.order;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.user.User;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    // 주문 생성
    public Order createOrder(User user, List<OrderItem> orderItems, UserCoupon userCoupon) {

        // 전체 가격 계산 로직
        double totalAmount = orderItems.stream()
            .mapToDouble(orderItem -> orderItem.getItem().getPrice() * orderItem.getQuantity())
            .sum();

        // 쿠폰 적용하여 최종 가격 계산 로직
        double finalAmount = totalAmount;
        if (userCoupon != null && userCoupon.getCoupon() != null) {
            double discountPercent = userCoupon.getCoupon().getDiscountPercent();
            finalAmount = totalAmount * (1 - discountPercent / 100); // 할인 비율을 적용하여 최종 금액 계산
        }

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount((long) totalAmount);
        order.setFinalAmount((long) finalAmount);
        order.setStatus("PENDING"); // 주문 생성 시 PENDING 상태로 생성

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
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ id.toString() }));

        order.setStatus(status);

        return orderRepository.save(order);
    }
}
