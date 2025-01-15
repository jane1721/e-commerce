package com.jane.ecommerce.application.item;

import com.jane.ecommerce.domain.item.ItemService;
import com.jane.ecommerce.interfaces.dto.item.TopItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetTopItemsUseCase {

    private final ItemService itemService;

    public List<TopItemResponse> execute() {

        return itemService.getTopItems();
    }
}
