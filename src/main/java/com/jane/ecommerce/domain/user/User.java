package com.jane.ecommerce.domain.user;

import com.jane.ecommerce.domain.error.ErrorCode;
import com.jane.ecommerce.domain.BaseEntity;
import com.jane.ecommerce.domain.error.CustomException;
import com.jane.ecommerce.domain.cart.CartItem;
import com.jane.ecommerce.domain.coupon.UserCoupon;
import com.jane.ecommerce.domain.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
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

    private User(Long id, String username, String password, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.orders = new ArrayList<>();
        this.userCoupons = new ArrayList<>();
    }

    public static User create(String username, String password, BigDecimal balance) {
        return new User(null, username, password, balance);
    }

    public static User of(Long id, String username, String password, BigDecimal balance) {
        return new User(id, username, password, balance);
    }

    // 잔액 충전 메서드
    public void chargeBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER, new String[]{ String.valueOf(amount) });
        }
        this.balance = this.balance.add(amount);
    }

    // 잔액 차감 메서드
    public void deductBalance(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) { // 유저 잔액이 부족할 경우
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE, new String[]{ String.valueOf(this.id) });
        }
        this.balance = this.balance.subtract(amount);
    }
}
