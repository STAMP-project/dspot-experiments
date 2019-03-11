package com.example;


import org.junit.Assert;
import org.junit.Test;


public class CalculatorJUnit4Test {
    @Test
    public void testAdd() {
        Assert.assertEquals(42, Integer.sum(19, 23));
    }
}

