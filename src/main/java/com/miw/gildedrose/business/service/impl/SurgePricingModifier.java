package com.miw.gildedrose.business.service.impl;

import com.miw.gildedrose.business.service.PricingModifier;
import com.miw.gildedrose.data.cache.ItemViewCacheProvider;
import com.miw.gildedrose.data.entity.Item;

import java.time.Duration;
import java.time.LocalDateTime;

public class SurgePricingModifier implements PricingModifier {
    private final double pricingPercentAdjustment;
    private final int viewsThreshold;
    private final Duration durationThreshold;
    private final ItemViewCacheProvider itemViewCacheProvider;

    public SurgePricingModifier(double pricingPercentAdjustment, int viewsThreshold, Duration durationThreshold, ItemViewCacheProvider itemViewCacheProvider) {
        this.pricingPercentAdjustment = pricingPercentAdjustment;
        this.viewsThreshold = viewsThreshold;
        this.durationThreshold = durationThreshold;
        this.itemViewCacheProvider = itemViewCacheProvider;
    }

    @Override
    public int getModifiedPrice(Item item) {
        if (meetsSurgePricingCriteria(item)) {
            return (int) Math.round(item.getPrice() * (1 + pricingPercentAdjustment));
        }
        return item.getPrice();
    }

    private boolean meetsSurgePricingCriteria(Item item) {
        final LocalDateTime timeBoundary = LocalDateTime.now().minus(durationThreshold);
        final long views =
                itemViewCacheProvider.get(item.getId()).stream().filter(time -> !time.isBefore(timeBoundary)).count();
        return views > viewsThreshold;
    }
}
