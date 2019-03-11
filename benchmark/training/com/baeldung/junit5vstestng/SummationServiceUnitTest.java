package com.baeldung.junit5vstestng;


import java.util.List;
import java.util.function.BinaryOperator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;


public class SummationServiceUnitTest {
    private static List<Integer> numbers;

    @Test
    public void givenNumbers_sumEquals_thenCorrect() {
        int sum = SummationServiceUnitTest.numbers.stream().reduce(0, Integer::sum);
        Assert.assertEquals(6, sum);
    }

    @Ignore
    @Test
    public void givenEmptyList_sumEqualsZero_thenCorrect() {
        int sum = SummationServiceUnitTest.numbers.stream().reduce(0, Integer::sum);
        Assert.assertEquals(6, sum);
    }
}

