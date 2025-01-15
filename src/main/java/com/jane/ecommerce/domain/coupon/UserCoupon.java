package com.jane.ecommerce.domain.coupon;

import com.jane.ecommerce.base.entity.BaseEntity;
import com.jane.ecommerce.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "user_coupon")
@Entity
public class UserCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;

    private UserCoupon(Long id, User user, Coupon coupon, Boolean isUsed) {
        this.id = id;
        this.user = user;
        this.coupon = coupon;
        this.isUsed = isUsed;
    }

    public static UserCoupon create(User user, Coupon coupon, Boolean isUsed) {
        return new UserCoupon(null, user, coupon, isUsed);
    }

    public static UserCoupon of(Long id, User user, Coupon coupon, Boolean isUsed) {
        return new UserCoupon(id, user, coupon, isUsed);
    }
}
