package com.jane.ecommerce.domain.coupon;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.entity.BaseEntity;
import com.jane.ecommerce.base.exception.BaseCustomException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "coupon")
@Entity
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "discount_percent", nullable = false)
    private Long discountPercent;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private int quantity; // 남은 발급 수량

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCoupon> userCoupons;

    private Coupon(Long id, String code, Long discountPercent, LocalDateTime expiryDate, int quantity, List<UserCoupon> userCoupons) {
        this.id = id;
        this.code = code;
        this.discountPercent = discountPercent;
        this.expiryDate = expiryDate;
        this.quantity = quantity;
        this.userCoupons = userCoupons;
    }

    public static Coupon create(String code, Long discountPercent, LocalDateTime expiryDate, int quantity, List<UserCoupon> userCoupons) {
        return new Coupon(null, code, discountPercent, expiryDate, quantity, userCoupons);
    }

    public static Coupon of(Long id, String code, Long discountPercent, LocalDateTime expiryDate, int quantity, List<UserCoupon> userCoupons) {
        return new Coupon(id, code, discountPercent, expiryDate, quantity, userCoupons);
    }

    // 쿠폰 발급
    public void claim() {
        if (this.quantity <= 0) {
            throw new BaseCustomException(BaseErrorCode.CONFLICT);
        }

        // 쿠폰 수량 감소
        this.quantity--;
    }
}
