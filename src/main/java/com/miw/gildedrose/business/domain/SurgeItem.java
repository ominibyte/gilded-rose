package com.miw.gildedrose.business.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurgeItem {
    private Long id;
    private String name;
    private String description;
    private int price;
    private int quantity;
}
