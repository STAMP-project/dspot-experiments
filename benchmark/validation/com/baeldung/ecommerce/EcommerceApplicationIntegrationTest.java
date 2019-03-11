package com.baeldung.ecommerce;


import HttpMethod.GET;
import HttpStatus.CREATED;
import SpringBootTest.WebEnvironment;
import com.baeldung.ecommerce.controller.OrderController;
import com.baeldung.ecommerce.controller.ProductController;
import com.baeldung.ecommerce.model.Order;
import com.baeldung.ecommerce.model.Product;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EcommerceApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
public class EcommerceApplicationIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private ProductController productController;

    @Autowired
    private OrderController orderController;

    @Test
    public void contextLoads() {
        Assertions.assertThat(productController).isNotNull();
        Assertions.assertThat(orderController).isNotNull();
    }

    @Test
    public void givenGetProductsApiCall_whenProductListRetrieved_thenSizeMatchAndListContainsProductNames() {
        ResponseEntity<Iterable<Product>> responseEntity = restTemplate.exchange((("http://localhost:" + (port)) + "/api/products"), GET, null, new org.springframework.core.ParameterizedTypeReference<Iterable<Product>>() {});
        Iterable<Product> products = responseEntity.getBody();
        Assertions.assertThat(products).hasSize(7);
        MatcherAssert.assertThat(products, Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.is("TV Set"))));
        MatcherAssert.assertThat(products, Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.is("Game Console"))));
        MatcherAssert.assertThat(products, Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.is("Sofa"))));
        MatcherAssert.assertThat(products, Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.is("Icecream"))));
        MatcherAssert.assertThat(products, Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.is("Beer"))));
        MatcherAssert.assertThat(products, Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.is("Phone"))));
        MatcherAssert.assertThat(products, Matchers.hasItem(Matchers.hasProperty("name", CoreMatchers.is("Watch"))));
    }

    @Test
    public void givenGetOrdersApiCall_whenProductListRetrieved_thenSizeMatchAndListContainsProductNames() {
        ResponseEntity<Iterable<Order>> responseEntity = restTemplate.exchange((("http://localhost:" + (port)) + "/api/orders"), GET, null, new org.springframework.core.ParameterizedTypeReference<Iterable<Order>>() {});
        Iterable<Order> orders = responseEntity.getBody();
        Assertions.assertThat(orders).hasSize(0);
    }

    @Test
    public void givenPostOrder_whenBodyRequestMatcherJson_thenResponseContainsEqualObjectProperties() {
        final ResponseEntity<Order> postResponse = restTemplate.postForEntity((("http://localhost:" + (port)) + "/api/orders"), prepareOrderForm(), Order.class);
        Order order = postResponse.getBody();
        Assertions.assertThat(postResponse.getStatusCode()).isEqualByComparingTo(CREATED);
        MatcherAssert.assertThat(order, Matchers.hasProperty("status", CoreMatchers.is("PAID")));
        MatcherAssert.assertThat(order.getOrderProducts(), Matchers.hasItem(Matchers.hasProperty("quantity", CoreMatchers.is(2))));
    }
}

