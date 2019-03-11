package com.baeldung;


import org.junit.Assert;
import org.junit.Test;


public class ArithmeticFunctionTest {
    @Test
    public void test_addingIntegers_returnsSum() {
        Assert.assertEquals(22, Math.addExact(10, 12));
    }

    @Test
    public void test_multiplyingIntegers_returnsProduct() {
        Assert.assertEquals(120, Math.multiplyExact(10, 12));
    }

    @Test
    public void test_subtractingIntegers_returnsDifference() {
        Assert.assertEquals(2, Math.subtractExact(12, 10));
    }

    @Test
    public void test_minimumInteger() {
        Assert.assertEquals(10, Math.min(10, 12));
    }
}

