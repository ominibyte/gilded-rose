package com.miw.gildedrose.config;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.miw.gildedrose.business.service.PricingModifier;
import com.miw.gildedrose.business.service.impl.SurgePricingModifier;
import com.miw.gildedrose.data.cache.ItemViewCacheProvider;
import com.miw.gildedrose.data.repository.ItemRepository;
import com.miw.gildedrose.web.filter.ItemViewMetricFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.servlet.Filter;
import java.time.Duration;

@Configuration
public class AppConfig {
    @Value("${app.item.surge.pricing-percent-adjustment}")
    private double pricingPercentAdjustment;

    @Value("${app.item.surge.views-threshold}")
    private int viewsThreshold;

    @Value("${app.item.surge.duration-threshold}")
    private Duration durationThreshold;

    @Bean
    public PricingModifier pricingModifier(ItemViewCacheProvider itemViewCacheProvider) {
        return new SurgePricingModifier(pricingPercentAdjustment, viewsThreshold, durationThreshold, itemViewCacheProvider);
    }

    @Bean
    public Filter itemMetricFilter(ItemViewCacheProvider itemViewCacheProvider, ItemRepository itemRepository) {
        return new ItemViewMetricFilter(itemViewCacheProvider, itemRepository, durationThreshold);
    }

    @Bean
    @Primary
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }
}
