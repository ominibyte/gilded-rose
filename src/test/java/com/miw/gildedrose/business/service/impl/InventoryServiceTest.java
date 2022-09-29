package com.miw.gildedrose.business.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miw.gildedrose.business.domain.SurgeItem;
import com.miw.gildedrose.business.exception.InsufficientQuantityException;
import com.miw.gildedrose.business.exception.ItemNotFoundException;
import com.miw.gildedrose.business.service.InventoryService;
import com.miw.gildedrose.business.service.PricingModifier;
import com.miw.gildedrose.data.entity.Item;
import com.miw.gildedrose.data.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    private static final Item ITEM_1 = new Item(1L, "silk", "SILK", "Silk material", 10, 1);
    private static final Item ITEM_2 = new Item(2L, "cotton", "COTTON", "Cotton wool", 15, 5);
    private static final Item EMPTY_ITEM = new Item(3L, "linen", "LINEN", "Linen cloth", 33, 0);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ItemRepository repository;

    @Mock
    private PricingModifier pricingModifier;

    private InventoryService inventoryService;

    @BeforeEach
    public void setUp() {
        inventoryService = new InventoryServiceImpl(repository, pricingModifier);
    }

    @Test
    void whenWeGetAllItemsWithoutSurgePricingThenResponseIsSuccessful() {
        when(repository.findAll())
                .thenReturn(Arrays.asList(ITEM_1, ITEM_2));
        defaultPricingStub();

        final List<SurgeItem> items = inventoryService.getAllItems();
        assertThat(items, contains(mapToSurgeItem(ITEM_1, 10), mapToSurgeItem(ITEM_2, 15)));
        verify(repository, times(1)).findAll();
    }

    @Test
    void whenWeGetAllItemsThenSurgePricingShouldBeReflected() {
        when(repository.findAll())
                .thenReturn(Arrays.asList(ITEM_1, ITEM_2));
        surgeStub();

        final List<SurgeItem> items = inventoryService.getAllItems();
        assertThat(items, contains(mapToSurgeItem(ITEM_1, 11), mapToSurgeItem(ITEM_2, 15)));
        verify(repository, times(1)).findAll();
    }

    @Test
    void whenWeGetAnItemByNameThenSurgePricingIsReflected() {
        when(repository.findItemByNameCase(anyString()))
                .thenReturn(Optional.of(ITEM_1));
        surgeStub();

        final SurgeItem item = inventoryService.getItemByName("silk");
        assertThat(item, equalTo(mapToSurgeItem(ITEM_1, 11)));

        final ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        verify(repository, times(1)).findItemByNameCase(nameCaptor.capture());
        assertThat(nameCaptor.getValue(), equalTo("SILK"));
    }

    @Test
    void whenWeTryToGetAnItemThatDoesNotExistThenExceptionIsThrown() {
        when(repository.findItemByNameCase(anyString()))
                .thenReturn(Optional.empty());

        final Throwable thrown = assertThrows(ItemNotFoundException.class, () -> inventoryService.getItemByName("silk"));
        assertThat(thrown.getMessage(), containsString("Unable to find an item with the name - "));
        verify(repository, times(1)).findItemByNameCase("SILK");
    }

    @Test
    void whenWeTryToPurchaseAnItemThatDoesNotExistThenExceptionIsThrown() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final Throwable thrown = assertThrows(ItemNotFoundException.class, () -> inventoryService.purchaseItem(1L));
        assertThat(thrown.getMessage(), containsString("Unable to find an item with the id - "));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void whenWeTryToPurchaseAnItemWithNoQuantityThenExceptionIsThrown() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(EMPTY_ITEM));

        assertThrows(InsufficientQuantityException.class, () -> inventoryService.purchaseItem(3L));
        verify(repository, times(1)).findById(3L);
    }

    @Test
    void whenWePurchaseAnItemThenProcessIsSuccessful() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(objectMapper.convertValue(ITEM_1, Item.class)));
        defaultPricingStub();
        doNothing().when(repository).decrementItemQuantity(anyLong());

        final Item item = objectMapper.convertValue(ITEM_1, Item.class);
        item.decrementQuantity();

        final SurgeItem surgeItem = inventoryService.purchaseItem(1L);
        assertThat(surgeItem, equalTo(mapToSurgeItem(item, 10)));
        assertThat(surgeItem.getQuantity(), equalTo(0));

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).decrementItemQuantity(1L);
        verifyNoMoreInteractions(repository);
    }

    private SurgeItem mapToSurgeItem(Item item, int price) {
        return new SurgeItem(
                item.getId(),
                item.getName(),
                item.getDescription(),
                price,
                item.getQuantity()
        );
    }

    private void defaultPricingStub() {
        doAnswer(inv -> ((Item) inv.getArgument(0)).getPrice())
                .when(pricingModifier).getModifiedPrice(any(Item.class));
    }

    private void surgeStub() {
        doAnswer(inv -> {
            final Item item = inv.getArgument(0);
            if (Objects.equals(item.getId(), ITEM_1.getId())) {
                return (int) Math.round(item.getPrice() * 1.1);
            }
            return item.getPrice();
        }).when(pricingModifier).getModifiedPrice(any(Item.class));
    }
}
