package com.miw.gildedrose.data.cache;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.UnaryOperator;

public interface ItemViewCacheProvider {
    List<LocalDateTime> get(Long id);
    List<LocalDateTime> evictAndUpdate(Long id, UnaryOperator<List<LocalDateTime>> evictionFunction);
}
