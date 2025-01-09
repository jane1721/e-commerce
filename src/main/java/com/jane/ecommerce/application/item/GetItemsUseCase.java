package com.jane.ecommerce.application.item;

import com.jane.ecommerce.domain.item.ItemService;
import com.jane.ecommerce.interfaces.dto.item.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetItemsUseCase {

    private final ItemService itemService;

    public Page<ItemResponse> execute(Pageable pageable) {
        return itemService.getItems(pageable).map(ItemResponse::from);
    }
}
