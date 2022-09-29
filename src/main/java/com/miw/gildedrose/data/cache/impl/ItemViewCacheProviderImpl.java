package com.miw.gildedrose.data.cache.impl;

import com.hazelcast.core.HazelcastInstance;
import com.miw.gildedrose.data.cache.ItemViewCacheProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

@Component
public class ItemViewCacheProviderImpl implements ItemViewCacheProvider {
    private static final String ITEMS = "items";

    private final HazelcastInstance hazelcastInstance;

    public ItemViewCacheProviderImpl(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public List<LocalDateTime> get(Long id) {
        final Map<Long, List<LocalDateTime>> map = hazelcastInstance.getMap(ITEMS);
        return map.getOrDefault(id, Collections.emptyList());
    }

    @Override
    public List<LocalDateTime> evictAndUpdate(Long id, UnaryOperator<List<LocalDateTime>> evictionFunction) {
        if (evictionFunction == null) {
            evictionFunction = UnaryOperator.identity();
        }
        final Map<Long, List<LocalDateTime>> map = hazelcastInstance.getMap(ITEMS);
        final List<LocalDateTime> timeEntries = evictionFunction.apply(new ArrayList<>(map.getOrDefault(id,
                Collections.emptyList())));
        timeEntries.add(LocalDateTime.now());
        return map.put(id, timeEntries);
    }
}
