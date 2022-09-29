package com.miw.gildedrose;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = GildedRoseApp.class, webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class GildedRoseAppTest {

    private final ApplicationContext context;

    @Autowired
    public GildedRoseAppTest(final ApplicationContext context) {
        this.context = context;
    }

    @Test
    void contextShouldLoadCorrectly() {
        assertThat(context).isNotNull();
    }
}
