package com.annimon.stream.intstreamtests;


import com.annimon.stream.CustomOperators;
import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntBinaryOperator;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class CustomTest {
    @Test(expected = NullPointerException.class)
    public void testCustomNull() {
        IntStream.empty().custom(null);
    }

    @Test
    public void testCustomIntermediateOperator_Zip() {
        final IntBinaryOperator op = new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left + right;
            }
        };
        IntStream s1 = IntStream.of(1, 3, 5, 7, 9);
        IntStream s2 = IntStream.of(2, 4, 6, 8);
        int[] expected = new int[]{ 3, 7, 11, 15 };
        IntStream result = s1.custom(new CustomOperators.Zip(s2, op));
        Assert.assertThat(result.toArray(), CoreMatchers.is(expected));
    }

    @Test
    public void testCustomTerminalOperator_Average() {
        double average = IntStream.range(0, 10).custom(new CustomOperators.Average());
        Assert.assertThat(average, Matchers.closeTo(4.5, 0.001));
    }
}

