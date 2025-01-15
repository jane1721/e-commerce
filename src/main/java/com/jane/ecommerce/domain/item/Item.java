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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "item")
@Entity
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    private Item(Long id, String name, BigDecimal price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.orderItems = new ArrayList<>();
        this.cartItems = new ArrayList<>();
    }

    public static Item create(String name, BigDecimal price, Integer stock) {
        return new Item(null, name, price, stock);
    }

    public static Item of(Long id, String name, BigDecimal price, Integer stock) {
        return new Item(id, name, price, stock);
    }

    // 재고 차감
    public void decreaseStock(int quantity) {
        // 재고가 부족한 경우 예외 발생
        if (this.stock < quantity) {
            throw new BaseCustomException(BaseErrorCode.INSUFFICIENT_STOCK, new String[]{ this.id.toString() });
        }
        this.stock -= quantity;
    }

    // 재고 복구
    public void restoreStock(int quantity) {
        // 음수로 재고 복구 시도할 경우 예외 발생
        if (quantity < 0) {
            throw new BaseCustomException(BaseErrorCode.INVALID_PARAMETER);
        }

        this.stock += quantity;  // 주문이 취소될 때, 차감된 수량만큼 복구
    }
}
