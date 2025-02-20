package com.jane.ecommerce.application.payment;

import com.jane.ecommerce.domain.order.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentProcessedEvent extends ApplicationEvent {

    private final Order order;

    public PaymentProcessedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }
}
