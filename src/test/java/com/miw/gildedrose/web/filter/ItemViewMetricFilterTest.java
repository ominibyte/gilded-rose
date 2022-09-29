package com.miw.gildedrose.web.filter;

import com.miw.gildedrose.data.cache.ItemViewCacheProvider;
import com.miw.gildedrose.data.entity.Item;
import com.miw.gildedrose.data.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemViewMetricFilterTest {
    private static final Item ITEM = new Item(1L, "silk", "SILK", "Silk material", 10, 1);
    private static final Duration DURATION_THRESHOLD = Duration.ofHours(1);

    @Mock
    private ItemRepository repository;

    @Mock
    private ItemViewCacheProvider itemViewCacheProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private FilterChain filterChain;

    private Filter filter;

    @BeforeEach
    public void setUp() {
        filter = new ItemViewMetricFilter(itemViewCacheProvider, repository, DURATION_THRESHOLD);
    }

    @Test
    void whenRequestIsNotItemAccessRequestThenDoNothing() throws ServletException, IOException {
        when(request.getMethod())
                .thenReturn("POST");
        filter.doFilter(request, null, filterChain);
        verifyNoInteractions(repository);
        verifyNoInteractions(itemViewCacheProvider);
        verify(filterChain, times(1)).doFilter(eq(request), any());
    }

    @Test
    void whenRequestIsAnItemAccessRequestThenCacheIsUpdated() throws ServletException, IOException {
        when(request.getMethod())
                .thenReturn("GET");
        when(request.getRequestURI())
                .thenReturn("/inventory/items/silk");
        when(repository.findItemByNameCase(anyString()))
                .thenReturn(Optional.of(ITEM));
        doReturn(Collections.emptyList()).when(itemViewCacheProvider).evictAndUpdate(anyLong(), any());

        filter.doFilter(request, null, filterChain);
        verify(repository, times(1)).findItemByNameCase("SILK");
        verify(itemViewCacheProvider, times(1)).evictAndUpdate(eq(1L), any());
    }
}
