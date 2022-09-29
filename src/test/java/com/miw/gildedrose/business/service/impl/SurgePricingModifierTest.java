package com.miw.gildedrose.business.service.impl;

import com.miw.gildedrose.business.service.PricingModifier;
import com.miw.gildedrose.data.cache.ItemViewCacheProvider;
import com.miw.gildedrose.data.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurgePricingModifierTest {
    private static final Item ITEM = new Item(1L, "silk", "SILK", "Silk material", 10, 1);
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final int VIEWS_THRESHOLD = 2;
    private static final Duration DURATION_THRESHOLD = Duration.ofHours(1);
    private static final LocalDateTime OUTDATED = LocalDateTime.now().minus(DURATION_THRESHOLD.multipliedBy(2));

    @Mock
    private ItemViewCacheProvider itemViewCacheProvider;

    private PricingModifier pricingModifier;

    @BeforeEach
    public void setUp() {
        pricingModifier = new SurgePricingModifier(0.1, VIEWS_THRESHOLD, DURATION_THRESHOLD, itemViewCacheProvider);
    }

    @Test
    void whenSurgePricingCriteriaIsReachedThenSurgePriceIsReturned() {
        when(itemViewCacheProvider.get(anyLong()))
                .thenReturn(Arrays.asList(NOW, NOW, NOW));
        assertThat(pricingModifier.getModifiedPrice(ITEM), equalTo(11));
        verify(itemViewCacheProvider, times(1)).get(ITEM.getId());
    }

    @Test
    void whenSurgePricingCriteriaIsNotReachedThenOriginalPriceIsReturned() {
        when(itemViewCacheProvider.get(anyLong()))
                .thenReturn(Collections.emptyList());
        assertThat(pricingModifier.getModifiedPrice(ITEM), equalTo(10));
        verify(itemViewCacheProvider, times(1)).get(ITEM.getId());
    }

    @Test
    void whenSurgePricingCriteriaIsAtTheThresholdThenOriginalPriceIsReturned() {
        when(itemViewCacheProvider.get(anyLong()))
                .thenReturn(Arrays.asList(NOW, NOW));
        assertThat(pricingModifier.getModifiedPrice(ITEM), equalTo(10));
        verify(itemViewCacheProvider, times(1)).get(ITEM.getId());
    }

    @Test
    void outdatedTimesShouldBeIgnored() {
        when(itemViewCacheProvider.get(anyLong()))
                .thenReturn(Arrays.asList(OUTDATED, OUTDATED, OUTDATED, NOW));
        assertThat(pricingModifier.getModifiedPrice(ITEM), equalTo(10));
        verify(itemViewCacheProvider, times(1)).get(ITEM.getId());
    }
}
