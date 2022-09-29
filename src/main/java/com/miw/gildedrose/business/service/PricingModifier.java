package com.miw.gildedrose.business.service;

import com.miw.gildedrose.data.entity.Item;

public interface PricingModifier {
    int getModifiedPrice(Item item);
}
