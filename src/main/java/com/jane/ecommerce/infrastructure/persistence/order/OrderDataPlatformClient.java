package com.jane.ecommerce.infrastructure.persistence.order;

import com.jane.ecommerce.domain.order.Order;
import com.jane.ecommerce.domain.order.OrderClient;

public class OrderDataPlatformClient implements OrderClient {

    @Override
    public boolean send(Order order) {
        return true;
    }
}
