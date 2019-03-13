package com.insightfullogic.java8.exercises.chapter6;


import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public class SerialToParallelTest {
    @Test
    public void testSerialToParallel() {
        IntStream range = IntStream.range(0, 100);
        Assert.assertEquals(328350, SerialToParallel.sumOfSquares(range));
    }
}

