package com.miw.gildedrose.web.application;

import com.miw.gildedrose.business.domain.SurgeItem;
import com.miw.gildedrose.business.service.InventoryService;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {
    private static final SurgeItem ITEM = new SurgeItem(1L, "silk", "Silk material", 10, 1);
    private static final Matcher<Boolean> isTrue = equalTo(true);

    @Mock
    private InventoryService service;

    private InventoryController controller;

    @BeforeEach
    public void setUp() {
        controller = new InventoryController(service);
    }

    @Test
    void getAllItemsReturnsSameObjectFromService() {
        final List<SurgeItem> items = Collections.singletonList(ITEM);
        when(service.getAllItems())
                .thenReturn(items);

        assertThat(items == controller.getAllItems(), isTrue);
    }

    @Test
    void getItemByNameReturnsSameObjectFromService() {
        when(service.getItemByName(anyString()))
                .thenReturn(ITEM);

        assertThat(ITEM == controller.getItemByName("silk"), isTrue);
    }

    @Test
    void purchaseItemReturnsSameObjectFromService() {
        when(service.purchaseItem(anyLong()))
                .thenReturn(ITEM);

        assertThat(ITEM == controller.purchaseItem(1L), isTrue);
    }
}
