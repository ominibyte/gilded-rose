package com.miw.gildedrose.web.filter;

import com.miw.gildedrose.data.cache.ItemViewCacheProvider;
import com.miw.gildedrose.data.entity.Item;
import com.miw.gildedrose.data.repository.ItemRepository;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemViewMetricFilter implements Filter {
    private final ItemViewCacheProvider itemViewCacheProvider;
    private final ItemRepository repository;
    private final Duration durationThreshold;

    public ItemViewMetricFilter(ItemViewCacheProvider itemViewCacheProvider, ItemRepository repository,
                                Duration durationThreshold) {
        this.itemViewCacheProvider = itemViewCacheProvider;
        this.repository = repository;
        this.durationThreshold = durationThreshold;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (isItemAccessRequest(servletRequest)) {
            final Long itemId = extractItemId(servletRequest);
            if (itemId != null) {
                itemViewCacheProvider.evictAndUpdate(itemId, this::doEviction);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isItemAccessRequest(ServletRequest servletRequest) {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        return request.getMethod().equalsIgnoreCase("GET") && request.getRequestURI().matches(
                "/inventory/items/.*");
    }

    private Long extractItemId(ServletRequest servletRequest) {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final Pattern pattern = Pattern.compile(".+/(.*)");
        final Matcher matcher = pattern.matcher(request.getRequestURI());

        if (matcher.find()) {
            final String itemName = matcher.group(1);
            return repository.findItemByNameCase(itemName.toUpperCase()).map(Item::getId).orElse(null);
        }
        return null;
    }

    private List<LocalDateTime> doEviction(List<LocalDateTime> timeEntries) {
        final LocalDateTime timeBoundary = LocalDateTime.now().minus(durationThreshold);
        timeEntries.removeIf(time -> time.isBefore(timeBoundary));
        return timeEntries;
    }
}
