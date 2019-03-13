package com.shekhargulati.java8_tutorial.ch01;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CalculatorTest {
    Calculator calculator = Calculator.getInstance();

    @Test
    public void shouldAddTwoNumbers() throws Exception {
        int sum = calculator.add(1, 2);
        Assert.assertThat(sum, CoreMatchers.is(CoreMatchers.equalTo(3)));
    }

    @Test
    public void shouldSubtractTwoNumbers() throws Exception {
        int difference = calculator.subtract(3, 2);
        Assert.assertThat(difference, CoreMatchers.is(CoreMatchers.equalTo(1)));
    }

    @Test
    public void shouldDivideTwoNumbers() throws Exception {
        int quotient = calculator.divide(4, 2);
        Assert.assertThat(quotient, CoreMatchers.is(CoreMatchers.equalTo(2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenDivisorIsZero() throws Exception {
        calculator.divide(4, 0);
    }

    @Test
    public void shouldMultiplyTwoNumbers() throws Exception {
        int product = calculator.multiply(3, 4);
        Assert.assertThat(product, CoreMatchers.is(CoreMatchers.equalTo(12)));
    }

    @Test
    public void shouldFindRemainderOfTwoNumbers() throws Exception {
        int remainder = calculator.remainder(100, 19);
        Assert.assertThat(remainder, CoreMatchers.is(CoreMatchers.equalTo(5)));
    }
}

