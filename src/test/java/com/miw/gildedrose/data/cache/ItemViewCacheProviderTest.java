package com.miw.gildedrose.data.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.miw.gildedrose.data.cache.impl.ItemViewCacheProviderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemViewCacheProviderTest {
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final Duration DURATION_THRESHOLD = Duration.ofHours(1);
    private static final LocalDateTime OUTDATED = LocalDateTime.now().minus(DURATION_THRESHOLD.multipliedBy(2));

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private IMap<Object, Object> map;

    private ItemViewCacheProvider itemViewCacheProvider;

    @BeforeEach
    public void setUp() {
        itemViewCacheProvider = new ItemViewCacheProviderImpl(hazelcastInstance);
    }

    @Test
    void getReturnsExpectedResult() {
        stubMap(Collections.singletonList(NOW));
        when(hazelcastInstance.getMap("items"))
                .thenReturn(map);

        assertThat(itemViewCacheProvider.get(1L), equalTo(Collections.singletonList(NOW)));
    }

    @Test
    void evictAndUpdateWithDefaultEvictionFunctionReturnsExpectedResult() {
        stubMap(Collections.singletonList(NOW));
        doAnswer(inv -> inv.getArgument(1)).when(map).put(anyLong(), anyList());
        when(hazelcastInstance.getMap("items"))
                .thenReturn(map);

        final List<LocalDateTime> entries = itemViewCacheProvider.evictAndUpdate(1L, null);
        assertThat(entries.size(), equalTo(2));
        verify(map, times(1)).put(1L, entries);
    }

    @Test
    void evictAndUpdateAppliesEvictionFunctionAndReturnsExpectedResult() {
        stubMap(Arrays.asList(OUTDATED, OUTDATED, NOW));
        doAnswer(inv -> inv.getArgument(1)).when(map).put(anyLong(), anyList());
        when(hazelcastInstance.getMap("items"))
                .thenReturn(map);

        final List<LocalDateTime> entries = itemViewCacheProvider.evictAndUpdate(1L, this::evictionFunction);
        assertThat(entries.size(), equalTo(2));
        verify(map, times(1)).put(1L, entries);
    }

    private List<LocalDateTime> evictionFunction(List<LocalDateTime> timeEntries) {
        final LocalDateTime timeBoundary = LocalDateTime.now().minus(DURATION_THRESHOLD);
        timeEntries.removeIf(time -> time.isBefore(timeBoundary));
        return timeEntries;
    }

    private void stubMap(List<LocalDateTime> entry) {
        doAnswer(inv -> {
            if (inv.getArgument(0, Long.class) == 1L) {
                return entry;
            }
            return Collections.emptyList();
        }).when(map).getOrDefault(eq(1L), any());
    }
}
