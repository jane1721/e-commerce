package com.jane.ecommerce.domain.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Page<Item> getItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }
}
