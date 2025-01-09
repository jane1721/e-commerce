package com.jane.ecommerce.domain.item;

import java.util.Optional;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.exception.BaseCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    // 상품 목록 페이징 조회
    public Page<Item> getItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    // 특정 상품 조회
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new BaseCustomException(BaseErrorCode.NOT_FOUND, new String[]{ String.valueOf(itemId) }));
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }
}
