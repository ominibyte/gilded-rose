package com.miw.gildedrose.web.application;

import com.miw.gildedrose.business.domain.SurgeItem;
import com.miw.gildedrose.business.service.InventoryService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/inventory", produces = "application/json")
public class InventoryController {
    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/items")
    public List<SurgeItem> getAllItems() {
        return service.getAllItems();
    }

    @GetMapping("/items/{name}")
    public SurgeItem getItemByName(@PathVariable("name") String name) {
        return service.getItemByName(name);
    }

    // Could also have used @PreAuthorize but this is simple enough
    @PostMapping("/purchase/{id}")
    @Secured("ROLE_USER")
    public SurgeItem purchaseItem(@PathVariable("id") Long id) {
        return service.purchaseItem(id);
    }
}
