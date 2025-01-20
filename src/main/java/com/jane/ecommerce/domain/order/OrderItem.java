package com.jane.ecommerce.domain.order;

import com.jane.ecommerce.domain.BaseEntity;
import com.jane.ecommerce.domain.item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer quantity;

    public void setOrder(Order order) {
        this.order = order;
    }

    private OrderItem(Long id, Order order, Item item, Integer quantity) {
        this.id = id;
        this.order = order;
        this.item = item;
        this.quantity = quantity;
    }

    public static OrderItem create(Item item, Integer quantity) {
        return new OrderItem(null, null, item, quantity);
    }

    public static OrderItem of(Long id, Order order, Item item, Integer quantity) {
        return new OrderItem(id, order, item, quantity);
    }
}
