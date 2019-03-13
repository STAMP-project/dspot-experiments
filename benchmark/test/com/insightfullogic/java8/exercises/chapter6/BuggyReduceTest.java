package com.insightfullogic.java8.exercises.chapter6;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class BuggyReduceTest {
    @Test
    public void sample() {
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        int result = BuggyReduce.multiplyThrough(numbers);
        Assert.assertEquals(30, result);
    }
}

