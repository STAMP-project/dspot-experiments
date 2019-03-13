package com.baeldung.ddd.order.jpa;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;


@SpringJUnitConfig
@SpringBootTest
public class PersistOrderIntegrationTest {
    @Autowired
    private JpaOrderRepository repository;

    @DisplayName("given order with two line items, when persist, then order is saved")
    @Test
    public void test() throws Exception {
        // given
        JpaOrder order = prepareTestOrderWithTwoLineItems();
        // when
        JpaOrder savedOrder = repository.save(order);
        // then
        JpaOrder foundOrder = repository.findById(savedOrder.getId()).get();
        assertThat(foundOrder.getOrderLines()).hasSize(2);
    }
}

