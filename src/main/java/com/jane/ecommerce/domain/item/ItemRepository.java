package com.jane.ecommerce.domain.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepository {
    Page<Item> findAll(Pageable pageable);
}
