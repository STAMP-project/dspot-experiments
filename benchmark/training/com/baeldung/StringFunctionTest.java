package com.baeldung;


import org.junit.Assert;
import org.junit.Test;


public class StringFunctionTest {
    @Test
    public void test_upperCase() {
        Assert.assertEquals("TESTCASE", "testCase".toUpperCase());
    }

    @Test
    public void test_indexOf() {
        Assert.assertEquals(1, "testCase".indexOf("e"));
    }
}

