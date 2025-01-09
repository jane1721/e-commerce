package com.jane.ecommerce.infrastructure.persistence.item;

import com.jane.ecommerce.domain.item.Item;
import com.jane.ecommerce.domain.item.ItemRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository itemJpaRepository;

    @Override
    public Page<Item> findAll(Pageable pageable) {
        return itemJpaRepository.findAll(pageable);
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return itemJpaRepository.findById(itemId);
    }
}
