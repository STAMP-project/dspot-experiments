package com.baeldung.enums;


import junit.framework.TestCase;
import org.junit.Test;

import static PizzaStatusEnum.READY;


public class PizzaUnitTest {
    @Test
    public void whenConvertedIntoEnum_thenGetsConvertedCorrectly() {
        String pizzaEnumValue = "READY";
        PizzaStatusEnum pizzaStatusEnum = PizzaStatusEnum.valueOf(pizzaEnumValue);
        TestCase.assertTrue((pizzaStatusEnum == (READY)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenConvertedIntoEnum_thenThrowsException() {
        String pizzaEnumValue = "rEAdY";
        PizzaStatusEnum pizzaStatusEnum = PizzaStatusEnum.valueOf(pizzaEnumValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenInvalidEnumValueContentWiseAsString_whenConvertedIntoEnum_thenThrowsException() {
        String pizzaEnumValue = "invalid";
        PizzaStatusEnum pizzaStatusEnum = PizzaStatusEnum.valueOf(pizzaEnumValue);
    }
}

