package com.baeldung.junit;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CalculatorUnitTest {
    Calculator calculator = new Calculator();

    @Test
    public void testAddition() {
        Assert.assertEquals("addition", 8, calculator.add(5, 3));
    }
}

