package com.baeldung.junit;


import org.junit.Assert;
import org.junit.Test;


public class AdditionUnitTest {
    Calculator calculator = new Calculator();

    @Test
    public void testAddition() {
        Assert.assertEquals("addition", 8, calculator.add(5, 3));
    }
}

