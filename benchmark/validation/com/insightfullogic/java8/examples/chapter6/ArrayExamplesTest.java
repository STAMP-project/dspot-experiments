package com.insightfullogic.java8.examples.chapter6;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ArrayExamplesTest {
    private final int size;

    private final double[] output;

    public ArrayExamplesTest(int size, double[] output) {
        this.size = size;
        this.output = output;
    }

    @Test
    public void parallel() {
        Assert.assertArrayEquals(output, ArrayExamples.parallelInitialize(size), 0.0);
    }

    @Test
    public void imperative() {
        Assert.assertArrayEquals(output, ArrayExamples.imperativeInitilize(size), 0.0);
    }
}

