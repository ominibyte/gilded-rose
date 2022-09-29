package com.miw.gildedrose.data.repository;

import com.miw.gildedrose.data.entity.Item;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
    Optional<Item> findItemByNameCase(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Item i SET i.quantity = i.quantity - 1 WHERE i.id = :id")
    void decrementItemQuantity(Long id);
}
