package com.clearspring.analytics.stream.cardinality;


import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestICardinalityAmpl {
    private int N = 1000000;

    private ICardinality cardinalityEstimator;

    private static Random prng = new Random();

    private static char[] hex = "0123456789abcdef".toCharArray();

    public TestICardinalityAmpl(ICardinality cardinalityEstimator) {
        super();
        this.cardinalityEstimator = cardinalityEstimator;
    }

    static int se = 0;

    public static Object streamElement(int i) {
        return Long.toHexString(TestICardinalityAmpl.prng.nextLong());

    }

    @Parameterized.Parameters
    public static Collection<Object[]> regExValues() {
        return Arrays.asList(new Object[][]{ new Object[]{ new AdaptiveCounting(16) } });
    }
}

