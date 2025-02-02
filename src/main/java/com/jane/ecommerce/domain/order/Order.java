package com.jane.ecommerce.domain.order;

import com.jane.ecommerce.domain.BaseEntity;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.payment.Payment;
import com.jane.ecommerce.domain.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Table(name = "order_info")
@NoArgsConstructor
@Entity
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_coupon_id")
    private UserCoupon userCoupon;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this); // 양방향 관계 설정
    }

    // 전체 주문 금액 계산
    public void calculateTotalAmount() {

        totalAmount = orderItems.stream()
                .map(orderItem -> orderItem.getItem().getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 쿠폰 적용하여 최종 주문 금액 계산
    public void calculateFinalAmount() {

        if (userCoupon != null && userCoupon.getCoupon() != null) {
            BigDecimal discountPercent = BigDecimal.valueOf(userCoupon.getCoupon().getDiscountPercent());
            BigDecimal discountFactor = BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            finalAmount = totalAmount.multiply(discountFactor); // 할인 비율을 적용하여 최종 금액 계산
        } else {
            finalAmount = totalAmount; // 쿠폰이 없는 경우 최종 금액은 전체 금액과 동일
        }
    }

    private Order(Long id, User user, UserCoupon userCoupon, BigDecimal totalAmount, BigDecimal finalAmount, OrderStatus status, List<OrderItem> orderItems, Payment payment) {
        this.id = id;
        this.user = user;
        this.userCoupon = userCoupon;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.status = status;
        this.orderItems = orderItems;
        this.payment = payment;
    }

    public static Order create(User user, UserCoupon userCoupon, BigDecimal totalAmount, BigDecimal finalAmount, OrderStatus status) {
        return new Order(null, user, userCoupon, totalAmount, finalAmount, status, new ArrayList<>(), null);
    }

    public static Order of(Long id, User user, BigDecimal totalAmount, BigDecimal finalAmount, OrderStatus status) {
        return new Order(id, user, null, totalAmount, finalAmount, status, new ArrayList<>(), null);
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
