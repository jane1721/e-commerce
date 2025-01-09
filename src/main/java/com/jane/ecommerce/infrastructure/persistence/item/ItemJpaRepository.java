package com.jane.ecommerce.infrastructure.persistence.item;

import com.jane.ecommerce.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {

}
