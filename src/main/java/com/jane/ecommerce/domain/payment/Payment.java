package com.jane.ecommerce.domain.payment;

import com.jane.ecommerce.base.entity.BaseEntity;
import com.jane.ecommerce.domain.order.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Long amount;

    @Column(nullable = false)
    private String method;

    @Column(nullable = false)
    private String status;
}
