package com.jane.ecommerce.application.order;

import com.jane.ecommerce.domain.coupon.CouponService;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.coupon.UserCouponService;
import com.jane.ecommerce.domain.item.Item;
import com.jane.ecommerce.domain.item.ItemService;
import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderItem;
import com.jane.ecommerce.domain.order.OrderService;
import com.jane.ecommerce.domain.user.User;
import com.jane.ecommerce.domain.user.UserService;
import com.jane.ecommerce.interfaces.dto.order.OrderCreateResponse;
import com.jane.ecommerce.interfaces.dto.order.OrderItemDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CreateOrderUseCase {

    private final OrderService orderService;
    private final UserService userService;
    private final ItemService itemService;
    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @Transactional
    public OrderCreateResponse execute(Long userId, List<OrderItemDTO> orderItemDTOs, Long userCouponId) {

        // userId 로 User 객체 조회
        User user = userService.getUserById(userId);

        // OrderItemDTO -> OrderItem 변환
        List<OrderItem> orderItems = orderItemDTOs.stream()
            .map(dto -> {
                Item item = itemService.getItemByIdWithLock(dto.getItemId()); // itemId 로 Item 조회

                // 재고 차감
                item.decreaseStock(dto.getQuantity());
                itemService.save(item); // 재고 업데이트

                return OrderItem.create(item, dto.getQuantity());
            })
            .collect(Collectors.toList());

        // userCouponId 로 UserCoupon 객체 조회
        UserCoupon userCoupon = null;
        if (userCouponId != null) {
            userCoupon = couponService.getUserCouponById(userCouponId);

            userCoupon.updateCouponIsUsed(true); // 유저 쿠폰 사용 처리
            userCouponService.save(userCoupon); // 유저 쿠폰 업데이트
        }

        // 주문 생성
        Order order = orderService.createOrder(user, orderItems, userCoupon);

        // OrderCreateResponse DTO 로 변환하여 반환
        return new OrderCreateResponse(order.getId(), order.getStatus(), order.getTotalAmount().intValue(), order.getCreatedAt());
    }
}
