package com.jane.ecommerce.domain.user;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.BaseEntity;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.cart.CartItem;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.order.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_account")
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private BigDecimal balance;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCoupon> userCoupons;

    // 잔액 충전 메서드
    public void chargeBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER, new String[]{ String.valueOf(amount) });
        }
        this.balance = this.balance.add(amount);
    }

    // 잔액 차감 메서드
    public void deductBalance(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE, new String[]{ String.valueOf(this.id) });
        }
        this.balance = this.balance.subtract(amount);
    }
}
