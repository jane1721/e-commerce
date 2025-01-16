package com.jane.ecommerce.domain.payment;

import com.jane.ecommerce.domain.BaseEntity;
import com.jane.ecommerce.domain.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Table(name = "payment")
@Entity
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String status;

    private Payment(Long id, Order order, BigDecimal amount, String method, String status) {
        this.id = id;
        this.order = order;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }

    public static Payment create(Order order, BigDecimal amount, String method, String status) {
        return new Payment(null, order, amount, method, status);
    }

    public static Payment of(Long id, Order order, BigDecimal amount, String method, String status) {
        return new Payment(id, order, amount, method, status);
    }
}
