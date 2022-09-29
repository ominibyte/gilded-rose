package com.miw.gildedrose.web.application;

import com.miw.gildedrose.data.entity.Item;
import com.miw.gildedrose.data.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class InventoryControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ItemRepository itemRepository;

    private String baseUrl;

    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        baseUrl = String.format("http://localhost:%d/inventory", port);
        restTemplate = new TestRestTemplate();

        itemRepository.saveAll(Arrays.asList(
                new Item(1L, "Silk", "SILK", "Silk material", 55, 1),
                new Item(2L, "Cotton", "COTTON", "Cotton wool", 90, 36),
                new Item(3L, "Linen", "LINEN", "Linen clothing", 133, 18)
        ));
        System.out.println(itemRepository.findAll());
    }

    @Test
    void whenWeRequestForAllItemsThenResponseIsSuccessful() {
        final ResponseEntity<List> response = restTemplate.getForEntity(baseUrl + "/items", List.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        final List items = response.getBody();
        assertThat(items, is(notNullValue()));
        assertThat(items.size(), equalTo(3));
        System.out.println(items);
        assertThat(((List<Map>) items).stream().map(item -> item.get("price")).collect(Collectors.toList()),
                contains(55, 90, 133));
        assertThat(((List<Map>) items).stream().map(item -> item.get("name")).collect(Collectors.toList()),
                contains("Silk", "Cotton", "Linen"));
    }

    @Test
    void whenWeRequestForAnItemThatDoesNotExistThenResponseIs404() {
        final ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/items/noexist", Map.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void whenWeRequestForAnItemThatExistsThenResponseIsSuccessful() {
        final ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/items/linen", Map.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        final Map data = response.getBody();
        assertThat(data, is(notNullValue()));
        assertThat(data.get("name"), equalTo("Linen"));
        assertThat(data.get("price"), equalTo(133));
        assertThat(data.get("quantity"), equalTo(18));
    }

    @Test
    void whenWeRequestForA_SurgeItemThenWeGetTheCorrectResponsePricing() {
        ResponseEntity<Map> response = null;
        for (int i = 0; i <= 11; i++) {
            response = restTemplate.getForEntity(baseUrl + "/items/silk", Map.class);
        }
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertResponseReturnsFirstItem(response, 61);
    }

    @Test
    void whenWeTryToPurchaseAnItemWithoutAuthenticationThenResponseIsUnauthorized() {
        final ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl + "/purchase/1",
                HttpEntity.EMPTY, Map.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void whenWeTryToPurchaseAnItemThatDoesNotExistThenResponseIs404() {
        final ResponseEntity<Map> response = restTemplate
                .withBasicAuth("admin", "hidden")
                .postForEntity(baseUrl + "/purchase/5000",
                HttpEntity.EMPTY, Map.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void whenWeTryToPurchaseAnItemThatExistsAndIsAvailableThenProcessIsSuccessful() {
        final ResponseEntity<Map> response = restTemplate
                .withBasicAuth("admin", "hidden")
                .postForEntity(baseUrl + "/purchase/1",
                        HttpEntity.EMPTY, Map.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertResponseReturnsFirstItem(response, 55);
    }

    @Test
    void whenWeTryToPurchaseAnItemThatIsNotAvailableThenProcessFails() {
        ResponseEntity<Map> response = null;
        for (int i = 0; i < 2; i++) {
            response = restTemplate
                    .withBasicAuth("admin", "hidden")
                    .postForEntity(baseUrl + "/purchase/1",
                            HttpEntity.EMPTY, Map.class);
        }
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    private void assertResponseReturnsFirstItem(ResponseEntity<Map> response, int price) {
        final Map data = response.getBody();
        assertThat(data, is(notNullValue()));
        assertThat(data.get("name"), equalTo("Silk"));
        assertThat(data.get("price"), equalTo(price));
        assertThat(data.get("quantity"), equalTo(1));
    }
}
