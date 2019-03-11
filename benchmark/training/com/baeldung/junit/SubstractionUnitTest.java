package com.baeldung.junit;


import org.junit.Assert;
import org.junit.Test;


public class SubstractionUnitTest {
    Calculator calculator = new Calculator();

    @Test
    public void substraction() {
        Assert.assertEquals("substraction", 2, calculator.sub(5, 3));
    }
}

