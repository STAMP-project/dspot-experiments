package com.baeldung;


import org.junit.Assert;
import org.junit.Test;


public class ComparisonFunctionTest {
    @Test
    public void test_findMax() {
        Assert.assertEquals(20, Math.max(10, 20));
    }

    @Test
    public void test_findMin() {
        Assert.assertEquals(10, Math.min(10, 20));
    }
}

