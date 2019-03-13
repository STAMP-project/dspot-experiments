package com.baeldung.junit4vstestng;


import java.util.List;
import java.util.function.BinaryOperator;
import org.junit.Assert;
import org.junit.Test;


public class SummationServiceIntegrationTest {
    private static List<Integer> numbers;

    @Test
    public void givenNumbers_sumEquals_thenCorrect() {
        int sum = SummationServiceIntegrationTest.numbers.stream().reduce(0, Integer::sum);
        Assert.assertEquals(6, sum);
    }

    @Test(expected = ArithmeticException.class)
    public void givenNumber_whenThrowsException_thenCorrect() {
        int i = 1 / 0;
    }
}

