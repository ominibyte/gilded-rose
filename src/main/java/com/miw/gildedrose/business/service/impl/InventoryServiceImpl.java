package com.miw.gildedrose.business.service.impl;

import com.miw.gildedrose.business.domain.SurgeItem;
import com.miw.gildedrose.business.exception.InsufficientQuantityException;
import com.miw.gildedrose.business.exception.ItemNotFoundException;
import com.miw.gildedrose.business.service.InventoryService;
import com.miw.gildedrose.business.service.PricingModifier;
import com.miw.gildedrose.data.entity.Item;
import com.miw.gildedrose.data.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final ItemRepository repository;
    private final PricingModifier pricingModifier;

    public InventoryServiceImpl(ItemRepository repository, PricingModifier pricingModifier) {
        this.repository = repository;
        this.pricingModifier = pricingModifier;
    }

    @Override
    public List<SurgeItem> getAllItems() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(this::mapToSurgeItem)
                .collect(Collectors.toList());
    }

    @Override
    public SurgeItem getItemByName(String name) {
        final Optional<Item> item = repository.findItemByNameCase(name.toUpperCase());
        return item.map(this::mapToSurgeItem).orElseThrow(() -> new ItemNotFoundException("Unable to find an item " +
                "with the name - \"" + name + "\""));
    }

    @Override
    public SurgeItem purchaseItem(Long id) {
        final Item item = repository.findById(id).orElseThrow(() -> new ItemNotFoundException("Unable to find an item " +
                "with the id - \"" + id + "\""));
        if (item.getQuantity() <= 0) {
            throw new InsufficientQuantityException();
        }

        repository.decrementItemQuantity(id);

        return mapToSurgeItem(item);
    }

    private SurgeItem mapToSurgeItem(Item item) {
        return new SurgeItem(
            item.getId(),
            item.getName(),
            item.getDescription(),
            pricingModifier.getModifiedPrice(item),
            item.getQuantity()
        );
    }
}
