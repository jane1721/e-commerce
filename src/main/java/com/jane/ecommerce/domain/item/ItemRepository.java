package com.jane.ecommerce.domain.item;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepository {
    Page<Item> findAll(Pageable pageable);
    Optional<Item> findById(Long itemId);
    Item save(Item item);
}
