package com.jane.ecommerce.domain.item;


import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.entity.BaseEntity;
import com.jane.ecommerce.base.exception.BaseCustomException;
import com.jane.ecommerce.domain.cart.CartItem;
import com.jane.ecommerce.domain.order.OrderItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
@Entity
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    public void decreaseStock(int quantity) {
        // 재고가 부족한 경우 예외 발생
        if (this.stock < quantity) {
            throw new BaseCustomException(BaseErrorCode.INSUFFICIENT_STOCK, new String[]{ this.id.toString() });
        }
        this.stock -= quantity;
    }
}
