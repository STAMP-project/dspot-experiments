package com.baeldung.nth.root.calculator;


import org.junit.Assert;
import org.junit.Test;


public class NthRootCalculatorUnitTest {
    private NthRootCalculator nthRootCalculator = new NthRootCalculator();

    @Test
    public void whenBaseIs125AndNIs3_thenNthRootIs5() {
        Double result = nthRootCalculator.calculate(125.0, 3.0);
        Assert.assertEquals(result, ((Double) (5.0)));
    }
}

