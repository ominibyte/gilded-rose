package com.miw.gildedrose.business.service;

import com.miw.gildedrose.business.domain.*;

import java.util.*;

public interface InventoryService {
    List<SurgeItem> getAllItems();

    SurgeItem getItemByName(String name);

    SurgeItem purchaseItem(Long id);
}
