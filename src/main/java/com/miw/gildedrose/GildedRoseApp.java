package com.miw.gildedrose;

import com.miw.gildedrose.data.entity.Item;
import com.miw.gildedrose.data.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@SpringBootApplication
public class GildedRoseApp {

    public static void main(String[] args) {
        SpringApplication.run(GildedRoseApp.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner commandLineRunner(ItemRepository itemRepository) {
        return args -> itemRepository.saveAll(Arrays.asList(
                new Item(1L, "Silk", "SILK", "Silk material", 55, 1),
                new Item(2L, "Cotton", "COTTON", "Cotton wool", 90, 36),
                new Item(3L, "Linen", "LINEN", "Linen clothing", 133, 18)
        ));
    }
}
