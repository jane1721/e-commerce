package com.jane.ecommerce.application.order;

import com.jane.ecommerce.domain.coupon.UserCouponService;
import com.jane.ecommerce.domain.item.Item;
import com.jane.ecommerce.domain.item.ItemService;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderItem;
import com.jane.ecommerce.domain.order.OrderService;
import com.jane.ecommerce.domain.order.OrderStatus;
import com.jane.ecommerce.interfaces.dto.order.OrderUpdateRequest;
import com.jane.ecommerce.interfaces.dto.order.OrderUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UpdateOrderStatusUseCase {

    private final OrderService orderService;
    private final ItemService itemService;
    private final UserCouponService userCouponService;

    @Transactional
    public OrderUpdateResponse execute(Long id, OrderUpdateRequest request) {
        Order order = orderService.updateOrderStatus(id, request.getStatus());

        // 주문 취소
        if (request.getStatus().equals(OrderStatus.CANCELLED)) {

            // 주문에 포함된 모든 상품의 재고 복구
            for (OrderItem orderItem : order.getOrderItems()) {
                Item item = orderItem.getItem();
                item.restoreStock(orderItem.getQuantity());  // 재고 복구
                itemService.save(item); // 재고 업데이트
            }

            if (order.getUserCoupon() != null) {

                order.getUserCoupon().updateCouponIsUsed(false); // 유저 쿠폰 미사용 처리
                userCouponService.save(order.getUserCoupon()); // 유저 쿠폰 업데이트
            }

            // 주문 상태 취소 변경
            orderService.updateOrderStatus(order.getId(), OrderStatus.CANCELLED);
        }

        return new OrderUpdateResponse(
                order.getId().toString(),
                order.getStatus(),
                order.getUpdatedAt()
        );
    }
}
