package com.baeldung.enums;


import Pizza.PizzaStatusEnum.DELIVERED;
import Pizza.PizzaStatusEnum.ORDERED;
import Pizza.PizzaStatusEnum.READY;
import com.baeldung.enums.Pizza.PizzaStatusEnum;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;


public class PizzaUnitTest {
    @Test
    public void givenPizaOrder_whenReady_thenDeliverable() {
        Pizza testPz = new Pizza();
        testPz.setStatus(READY);
        TestCase.assertTrue(testPz.isDeliverable());
    }

    @Test
    public void givenPizaOrders_whenRetrievingUnDeliveredPzs_thenCorrectlyRetrieved() {
        List<Pizza> pzList = new ArrayList<>();
        Pizza pz1 = new Pizza();
        pz1.setStatus(DELIVERED);
        Pizza pz2 = new Pizza();
        pz2.setStatus(ORDERED);
        Pizza pz3 = new Pizza();
        pz3.setStatus(ORDERED);
        Pizza pz4 = new Pizza();
        pz4.setStatus(READY);
        pzList.add(pz1);
        pzList.add(pz2);
        pzList.add(pz3);
        pzList.add(pz4);
        List<Pizza> undeliveredPzs = Pizza.getAllUndeliveredPizzas(pzList);
        TestCase.assertTrue(((undeliveredPzs.size()) == 3));
    }

    @Test
    public void givenPizaOrders_whenGroupByStatusCalled_thenCorrectlyGrouped() {
        List<Pizza> pzList = new ArrayList<>();
        Pizza pz1 = new Pizza();
        pz1.setStatus(DELIVERED);
        Pizza pz2 = new Pizza();
        pz2.setStatus(ORDERED);
        Pizza pz3 = new Pizza();
        pz3.setStatus(ORDERED);
        Pizza pz4 = new Pizza();
        pz4.setStatus(READY);
        pzList.add(pz1);
        pzList.add(pz2);
        pzList.add(pz3);
        pzList.add(pz4);
        EnumMap<Pizza.PizzaStatusEnum, List<Pizza>> map = Pizza.groupPizzaByStatus(pzList);
        TestCase.assertTrue(((map.get(DELIVERED).size()) == 1));
        TestCase.assertTrue(((map.get(ORDERED).size()) == 2));
        TestCase.assertTrue(((map.get(READY).size()) == 1));
    }

    @Test
    public void whenDelivered_thenPizzaGetsDeliveredAndStatusChanges() {
        Pizza pz = new Pizza();
        pz.setStatus(READY);
        pz.deliver();
        TestCase.assertTrue(((pz.getStatus()) == (PizzaStatusEnum.DELIVERED)));
    }
}

